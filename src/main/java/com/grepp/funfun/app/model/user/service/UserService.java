package com.grepp.funfun.app.model.user.service;

import com.grepp.funfun.app.controller.api.user.payload.OAuth2SignupRequest;
import com.grepp.funfun.app.controller.api.user.payload.SignupRequest;
import com.grepp.funfun.app.model.auth.AuthService;
import com.grepp.funfun.app.model.auth.dto.TokenDto;
import com.grepp.funfun.app.model.auth.token.RefreshTokenService;
import com.grepp.funfun.app.model.contact.entity.Contact;
import com.grepp.funfun.app.model.contact.repository.ContactRepository;
import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.model.preference.entity.ContentPreference;
import com.grepp.funfun.app.model.preference.entity.GroupPreference;
import com.grepp.funfun.app.model.preference.repository.ContentPreferenceRepository;
import com.grepp.funfun.app.model.preference.repository.GroupPreferenceRepository;
import com.grepp.funfun.app.model.report.entity.Report;
import com.grepp.funfun.app.model.report.repository.ReportRepository;
import com.grepp.funfun.app.model.social.entity.Follow;
import com.grepp.funfun.app.model.social.entity.Message;
import com.grepp.funfun.app.model.social.repository.FollowRepository;
import com.grepp.funfun.app.model.social.repository.MessageRepository;
import com.grepp.funfun.app.model.user.dto.UserDTO;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.entity.UserInfo;
import com.grepp.funfun.app.model.user.repository.UserInfoRepository;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.mail.MailTemplate;
import com.grepp.funfun.infra.mail.SmtpDto;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final FollowRepository followRepository;
    private final MessageRepository messageRepository;
    private final ContactRepository contactRepository;
    private final ReportRepository reportRepository;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;
    private final GroupPreferenceRepository groupPreferenceRepository;
    private final ContentPreferenceRepository contentPreferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailTemplate mailTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Value("${front-server.domain}")
    private String domain;


    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("email"));
        return users.stream()
            .map(user -> mapToDTO(user, new UserDTO()))
            .toList();
    }

    public UserDTO get(final String email) {
        return userRepository.findById(email)
            .map(user -> mapToDTO(user, new UserDTO()))
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    @Transactional
    public String create(SignupRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CommonException(ResponseCode.USER_EMAIL_DUPLICATE);
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CommonException(ResponseCode.USER_NICKNAME_DUPLICATE);
        }

        User user = request.toEntity();

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(request.getEmail());
        userInfoRepository.save(userInfo);
        user.setInfo(userInfo);

        String email = userRepository.save(user).getEmail();

        sendSignupVerificationMail(user);

        return email;
    }

    @Transactional
    public void resendVerificationSignupEmail(String email) {
        // 1. 유저 존재 여부 확인
        User user = userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        if (user.getIsVerified()) {
            throw new CommonException(ResponseCode.ALREADY_VERIFIED);
        }

        // 회원가입 인증 메일 쿨타임 검증
        validateEmailLimit(email, "signup");
        sendSignupVerificationMail(user);
    }

    private void sendSignupVerificationMail(User user) {
        // 메일 인증 코드 생성
        String code = UUID.randomUUID().toString();
        String key = "signup:" + code;
        redisTemplate.opsForValue().set(key, user.getEmail(), Duration.ofMinutes(5));

        SmtpDto smtpDto = new SmtpDto();
        smtpDto.setTo(user.getEmail());
        smtpDto.setTemplatePath("mail/signup-verification");
        smtpDto.setSubject("회원가입을 환영합니다!");
        smtpDto.setProperties(Map.of("domain", domain, "code", code, "nickname", user.getNickname()));

        mailTemplate.send(smtpDto);
    }

    @Transactional
    public TokenDto verifySignupCode(String code) {
        String key = "signup:" + code;
        String email = (String) redisTemplate.opsForValue().get(key);

        if (email == null) {
            throw new CommonException(ResponseCode.BAD_USER_VERIFY);
        }

        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        user.setIsVerified(true);
        userRepository.save(user);
        redisTemplate.delete(key);

        return authService.processTokenSignin(user.getEmail(), user.getRole().name(), false);
    }

    public void sendCode(String email) {
        // 1. 유저 존재 여부 확인
        User user = userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 2. 인증 코드 메일 쿨타임 검증
        validateEmailLimit(email, "code");
        // 3. 인증 코드 생성 및 저장 (5분)
        String code = String.format("%06d", new Random().nextInt(999_999));
        redisTemplate.opsForValue().set("auth-code:" + email, code, Duration.ofMinutes(5));

        SmtpDto smtpDto = new SmtpDto();
        smtpDto.setTo(user.getEmail());
        smtpDto.setTemplatePath("mail/code-verification");
        smtpDto.setSubject("FunFun 인증 코드");
        smtpDto.setProperties(Map.of("code", code));

        mailTemplate.send(smtpDto);
    }

    public void verifyCode(String email, String code) {
        String codeKey = "auth-code:" + email;
        String storedCode = (String) redisTemplate.opsForValue().get(codeKey);

        // 인증 코드 검증
        if (storedCode == null || !storedCode.equals(code)) {
            throw new CommonException(ResponseCode.INVALID_AUTH_CODE);
        }
        // 인증 코드 삭제
        redisTemplate.delete(codeKey);

        // 인증 코드 검증 성공한 이메일 저장 (10분)
        redisTemplate.opsForValue().set("auth-code:verified:" + email, "true", Duration.ofMinutes(10));
    }

    private void validateEmailLimit(String email, String type) {
        String cooldownKey = "auth-cooldown:" + type + ":" + email;

        // 쿨타임 체크
        if (redisTemplate.hasKey(cooldownKey)) {
            throw new CommonException(ResponseCode.TOO_FAST_VERIFY_REQUEST);
        }

        // 쿨타임 3분 설정
        redisTemplate.opsForValue().set(cooldownKey, "true", Duration.ofMinutes(3));
    }

    @Transactional
    public void changePassword(String email, String password) {
        String verifiedKey = "auth-code:verified:" +  email;
        String coolDownKey = "auth-cooldown:code:" +  email;
        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        if (!redisTemplate.hasKey(verifiedKey)) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // 레디스 인증 키 삭제
        redisTemplate.delete(verifiedKey);
        // 레디스 메일 쿨타임 키 삭제
        redisTemplate.delete(coolDownKey);
    }

    @Transactional
    public void updateOAuth2User(String email, OAuth2SignupRequest request) {
        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CommonException(ResponseCode.USER_NICKNAME_DUPLICATE);
        }
        request.toEntity(user);
        userRepository.save(user);
    }

    @Transactional
    public void changeNickname(String email, String nickname) {
        String verifiedKey = "auth-code:verified:" +  email;
        String coolDownKey = "auth-cooldown:code:" +  email;
        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        if (!redisTemplate.hasKey(verifiedKey)) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(nickname)) {
            throw new CommonException(ResponseCode.USER_NICKNAME_DUPLICATE);
        }

        user.setNickname(nickname);
        userRepository.save(user);

        // 레디스 인증 키 삭제
        redisTemplate.delete(verifiedKey);
        // 레디스 메일 쿨타임 키 삭제
        redisTemplate.delete(coolDownKey);
    }

    public void verifyNickname(String nickname) {
        // 닉네임 중복 검사
        if (userRepository.existsByNickname(nickname)) {
            throw new CommonException(ResponseCode.USER_NICKNAME_DUPLICATE);
        }
    }

    @Transactional
    public void unActive(String email, String accessTokenId) {
        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        user.unActivated();
        userRepository.save(user);

        refreshTokenService.deleteByAccessTokenId(accessTokenId);
    }

    public void update(final String email, final UserDTO userDTO) {
        final User user = userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final String email) {
        userRepository.deleteById(email);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setNickname(user.getNickname());
        userDTO.setGender(user.getGender());
        userDTO.setAddress(user.getAddress());
        userDTO.setRole(user.getRole());
        userDTO.setStatus(user.getStatus());
        userDTO.setDueDate(user.getDueDate());
        userDTO.setSuspendDuration(user.getSuspendDuration());
        userDTO.setDueReason(user.getDueReason());
        userDTO.setIsVerified(user.getIsVerified());
        userDTO.setIsMarketingAgreed(user.getIsMarketingAgreed());
        userDTO.setInfo(user.getInfo() == null ? null : user.getInfo().getEmail());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setPassword(userDTO.getPassword());
        user.setNickname(userDTO.getNickname());
        user.setGender(userDTO.getGender());
        user.setAddress(userDTO.getAddress());
        user.setRole(userDTO.getRole());
        user.setStatus(userDTO.getStatus());
        user.setDueDate(userDTO.getDueDate());
        user.setSuspendDuration(userDTO.getSuspendDuration());
        user.setDueReason(userDTO.getDueReason());
        user.setIsVerified(userDTO.getIsVerified());
        user.setIsMarketingAgreed(userDTO.getIsMarketingAgreed());
        final UserInfo info =
            userDTO.getInfo() == null ? null : userInfoRepository.findById(userDTO.getInfo())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        user.setInfo(info);
        return user;
    }

    public ReferencedWarning getReferencedWarning(final String email) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        final Follow followerFollow = followRepository.findFirstByFollower(user);
        if (followerFollow != null) {
            referencedWarning.setKey("user.follow.follower.referenced");
            referencedWarning.addParam(followerFollow.getId());
            return referencedWarning;
        }
        final Follow followeeFollow = followRepository.findFirstByFollowee(user);
        if (followeeFollow != null) {
            referencedWarning.setKey("user.follow.followee.referenced");
            referencedWarning.addParam(followeeFollow.getId());
            return referencedWarning;
        }
        final Message senderMessage = messageRepository.findFirstBySender(user);
        if (senderMessage != null) {
            referencedWarning.setKey("user.message.sender.referenced");
            referencedWarning.addParam(senderMessage.getId());
            return referencedWarning;
        }
        final Message receiverMessage = messageRepository.findFirstByReceiver(user);
        if (receiverMessage != null) {
            referencedWarning.setKey("user.message.receiver.referenced");
            referencedWarning.addParam(receiverMessage.getId());
            return referencedWarning;
        }
        final Contact userContact = contactRepository.findFirstByUser(user);
        if (userContact != null) {
            referencedWarning.setKey("user.contact.user.referenced");
            referencedWarning.addParam(userContact.getId());
            return referencedWarning;
        }
        final Report reportingUserReport = reportRepository.findFirstByReportingUser(user);
        if (reportingUserReport != null) {
            referencedWarning.setKey("user.report.reportingUser.referenced");
            referencedWarning.addParam(reportingUserReport.getId());
            return referencedWarning;
        }
        final Report reportedUserReport = reportRepository.findFirstByReportedUser(user);
        if (reportedUserReport != null) {
            referencedWarning.setKey("user.report.reportedUser.referenced");
            referencedWarning.addParam(reportedUserReport.getId());
            return referencedWarning;
        }
        final Group leaderGroup = groupRepository.findFirstByLeader(user);
        if (leaderGroup != null) {
            referencedWarning.setKey("user.group.leader.referenced");
            referencedWarning.addParam(leaderGroup.getId());
            return referencedWarning;
        }
        final Participant userParticipant = participantRepository.findFirstByUser(user);
        if (userParticipant != null) {
            referencedWarning.setKey("user.participant.user.referenced");
            referencedWarning.addParam(userParticipant.getId());
            return referencedWarning;
        }
        final GroupPreference userGroupPreference = groupPreferenceRepository.findFirstByUser(user);
        if (userGroupPreference != null) {
            referencedWarning.setKey("user.groupPreference.user.referenced");
            referencedWarning.addParam(userGroupPreference.getId());
            return referencedWarning;
        }
        final ContentPreference userContentPreference = contentPreferenceRepository.findFirstByUser(
            user);
        if (userContentPreference != null) {
            referencedWarning.setKey("user.contentPreference.user.referenced");
            referencedWarning.addParam(userContentPreference.getId());
            return referencedWarning;
        }
        return null;
    }

    // 유저 닉네임으로 찾기
    public UserDTO getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .map(user -> mapToDTO(user,new UserDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }
}

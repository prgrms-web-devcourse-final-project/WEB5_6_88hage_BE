package com.grepp.funfun.app.domain.user.service;

import com.grepp.funfun.app.domain.auth.dto.TokenDto;
import com.grepp.funfun.app.domain.auth.service.AuthService;
import com.grepp.funfun.app.domain.auth.token.RefreshTokenService;
import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.domain.participant.service.ParticipantService;
import com.grepp.funfun.app.domain.user.dto.UserDTO;
import com.grepp.funfun.app.domain.user.dto.payload.CoordinateResponse;
import com.grepp.funfun.app.domain.user.dto.payload.OAuth2SignupRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserInfoRequest;
import com.grepp.funfun.app.domain.user.dto.payload.SignupRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserInfoRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserInfoResponse;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.entity.UserInfo;
import com.grepp.funfun.app.domain.user.repository.UserInfoRepository;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.mail.MailTemplate;
import com.grepp.funfun.app.infra.mail.SmtpDto;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
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
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailTemplate mailTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final GroupService groupService;
    private final ParticipantService participantService;

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

    public UserInfoResponse getUserInfo(String email) {
        return userRepository.findById(email)
            .map(UserInfoResponse::from)
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

        User user = User.builder()
            .email(request.getEmail())
            .nickname(request.getNickname())
            .address(request.getAddress())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .birthDate(request.getBirthDate())
            .gender(request.getGender())
            .isMarketingAgreed(request.getIsMarketingAgreed())
            .role(Role.ROLE_USER)
            .isVerified(false)
            // 비밀번호 암호화
            .password(passwordEncoder.encode(request.getPassword()))
            .info(UserInfo.builder()
                .email(request.getEmail())
                .build())
            .build();

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

        SmtpDto smtpDto = SmtpDto.builder()
            .to(user.getEmail())
            .templatePath("mail/signup-verification")
            .subject("회원가입을 환영합니다!")
            .properties(Map.of(
                "domain", domain,
                "code", code,
                "nickname", user.getNickname()
            ))
            .build();

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
        user.verifyEmail();
        userRepository.save(user);
        redisTemplate.delete(key);

        return authService.processTokenLogin(user.getEmail(), user.getNickname(), user.getRole().name(), false);
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

        SmtpDto smtpDto = SmtpDto.builder()
            .to(user.getEmail())
            .templatePath("mail/code-verification")
            .subject("FunFun 인증 코드")
            .properties(Map.of("code", code))
            .build();

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
            throw new CommonException(ResponseCode.EXPIRED_AUTH_CODE_VERIFY);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        user.changePassword(encodedPassword);
        userRepository.save(user);

        // 레디스 인증 키 삭제
        redisTemplate.delete(verifiedKey);
        // 레디스 메일 쿨타임 키 삭제
        redisTemplate.delete(coolDownKey);
    }

    @Transactional
    public void updateUserInfo(String email, UserInfoRequest request) {
        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        user.updateUser(request);
    }

    @Transactional
    public TokenDto updateOAuth2User(String email, OAuth2SignupRequest request) {
        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CommonException(ResponseCode.USER_NICKNAME_DUPLICATE);
        }

        user.updateOAuth2User(request);

        return authService.reissueAccessToken(request.getNickname(), user.getRole().name());
    }

    @Transactional
    public TokenDto changeNickname(String email, String nickname) {
        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(nickname)) {
            throw new CommonException(ResponseCode.USER_NICKNAME_DUPLICATE);
        }
        user.changeNickname(nickname);
        userRepository.save(user);

        return authService.reissueAccessToken(nickname, user.getRole().name());
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

        // 1. 리더인 모임 전체 삭제
        groupService.deleteAllMyLeadGroups(email);

        // 2. 참여 중인 모임 전체 나가기
        participantService.leaveAllMyGroups(email);

        user.unActivated();
        userRepository.save(user);

        refreshTokenService.deleteByAccessTokenId(accessTokenId);
    }

    public CoordinateResponse getCoordinate(String email) {
        User user = userRepository.findById(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        return CoordinateResponse.builder()
            .email(email)
            .latitude(user.getLatitude())
            .longitude(user.getLongitude())
            .build();
    }

    @Transactional
    public void update(final String email, final UserDTO userDTO) {
        final User user = userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    @Transactional
    public void delete(final String email) {
        userRepository.deleteById(email);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setNickname(user.getNickname());
        userDTO.setBirthDate(user.getBirthDate());
        userDTO.setGender(user.getGender());
        userDTO.setAddress(user.getAddress());
        userDTO.setLatitude(user.getLatitude());
        userDTO.setLongitude(user.getLongitude());
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
        final UserInfo info =
            userDTO.getInfo() == null ? null : userInfoRepository.findById(userDTO.getInfo())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        user.updateFromDTO(userDTO, info);
        return user;
    }

    // 유저 닉네임으로 찾기
    public UserDTO getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .map(user -> mapToDTO(user,new UserDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    // 유저 취향을 문자열로 반환
    public String getUserPreferenceDescription(String email, String kind) {

        if(kind.equals("CONTENT")){
            User user = userRepository.findByEmailWithContentPreferences(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
            return user.getContentPreferencesToString();
        } else{
            User user = userRepository.findByEmailWithGroupPreferences(email).orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
            return user.getGroupPreferencesToString();
        }
    }

    public String getUserAgePromptByEmail(String email) {

        User user = userRepository.findByEmail(email);
        String birthDateStr = user.getBirthDate();

        // 문자열에서 년, 월, 일 추출
        int year = Integer.parseInt(birthDateStr.substring(0, 4));
        int month = Integer.parseInt(birthDateStr.substring(4, 6));
        int day = Integer.parseInt(birthDateStr.substring(6, 8));

        // LocalDate로 변환
        LocalDate birthDate = LocalDate.of(year, month, day);

        // 현재 날짜
        LocalDate today = LocalDate.now();

        // 만나이 계산 (Period 사용)
        Period period = Period.between(birthDate, today);
        int age = period.getYears();

        return "나는 만" + age + "세야. ";
    }

    public List<String> getAllUserEmails() {
        return userRepository.findAllEmails();
    }
}

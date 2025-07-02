package com.grepp.funfun.app.model.user.service;

import com.grepp.funfun.app.controller.api.user.payload.SignupRequest;
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
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

        return userRepository.save(user).getEmail();
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
        userDTO.setAge(user.getAge());
        userDTO.setGender(user.getGender());
        userDTO.setTel(user.getTel());
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
        user.setAge(userDTO.getAge());
        user.setGender(userDTO.getGender());
        user.setTel(userDTO.getTel());
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

}

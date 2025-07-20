package com.grepp.funfun.app.domain.integrate;

import static org.assertj.core.api.Assertions.assertThat;

import com.grepp.funfun.app.domain.auth.dto.payload.LoginRequest;
import com.grepp.funfun.app.domain.auth.service.AuthService;
import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.participant.service.ParticipantService;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserParticipantIntegrateTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // InjectMocks
    // 테스트 대상인 실제 Service 객체를 생성
    @Autowired
    private ParticipantService participantService;

    //todo 현재는 내부로직 관점으로 test 진행
    //todo 추가적으로 controller 도 포함시켜 API 레벨 통합 테스트 진행 예정
    @Test
    public void approveParticipant() {
        // GIVEN
        String rawPassword = "123qwe!@#";

        // 리더
        User leader = User.builder()
            .email("leader-test@aaa.aaa")
            .password(passwordEncoder.encode(rawPassword))
            .nickname("leader-test")
            .role(Role.ROLE_USER)
            .isVerified(true)
            .status(UserStatus.ACTIVE)
            .build();

        userRepository.save(leader);

        // 모임 생성
        Group group = Group.builder()
            .leader(leader)
            .maxPeople(5)
            .nowPeople(1)
            .status(GroupStatus.RECRUITING)
        .build();


        // 참가자
        User member = User.builder()
            .email("member-test@aaa.aaa")
            .password(passwordEncoder.encode(rawPassword))
            .nickname("member-test")
            .role(Role.ROLE_USER)
            .isVerified(true)
            .status(UserStatus.ACTIVE)
            .build();

        userRepository.save(member);
        groupRepository.save(group);

        //로그인 요청
        //member
        LoginRequest memberLoginRequest = new LoginRequest();
        memberLoginRequest.setEmail(member.getEmail());
        memberLoginRequest.setPassword(rawPassword);

        //로그인 실행
        authService.login(memberLoginRequest);
        System.out.println("로그인 완료");

        //모임 참여 신청
        participantService.apply(group.getId(),member.getEmail());

        assertThat(participantService.getPendingParticipants(group.getId()))
            .extracting("userEmail")
            .contains("member-test@aaa.aaa");

        List<String> memberPending = List.of("member-test@aaa.aaa");

        participantService.approveParticipant(group.getId(), memberPending, leader.getEmail());

        assertThat(participantService.getApproveParticipants(group.getId()))
            .extracting("userEmail")
            .contains("member-test@aaa.aaa");

        // 리더/멤버 2명인지 확인
        assertThat(group.getNowPeople()).isEqualTo(2);

        System.out.println("모임 신청/승인 완료");
    }

}

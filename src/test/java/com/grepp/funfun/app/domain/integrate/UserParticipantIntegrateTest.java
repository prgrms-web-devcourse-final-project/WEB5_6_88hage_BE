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
        User leader = new User();
        leader.setEmail("leader@aaa.aaa");
        leader.setPassword(passwordEncoder.encode(rawPassword));
        leader.setNickname("leader");
        leader.setRole(Role.ROLE_USER);
        leader.setIsVerified(true);
        leader.setStatus(UserStatus.ACTIVE);

        userRepository.save(leader);

        // 모임 생성
        Group group = new Group();
        group.setLeader(leader);
        group.setMaxPeople(5);
        group.setNowPeople(1);
        group.setStatus(GroupStatus.RECRUITING);

        // 참가자
        User member = new User();
        member.setEmail("member@aaa.aaa");
        member.setPassword(passwordEncoder.encode(rawPassword));
        member.setNickname("member");
        member.setRole(Role.ROLE_USER);
        member.setIsVerified(true);
        member.setStatus(UserStatus.ACTIVE);

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
            .contains("member@aaa.aaa");

        List<String> memberPending = List.of("member@aaa.aaa");

        participantService.approveParticipant(group.getId(), memberPending, leader.getEmail());

        assertThat(participantService.getApproveParticipants(group.getId()))
            .extracting("userEmail")
            .contains("member@aaa.aaa");

        // 리더/멤버 2명인지 확인
        assertThat(group.getNowPeople()).isEqualTo(2);

        System.out.println("모임 신청/승인 완료");
    }

}

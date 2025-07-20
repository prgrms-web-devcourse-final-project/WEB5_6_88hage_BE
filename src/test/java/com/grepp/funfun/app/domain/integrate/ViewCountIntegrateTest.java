package com.grepp.funfun.app.domain.integrate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.GroupHashtag;
import com.grepp.funfun.app.domain.group.repository.GroupHashtagRepository;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ViewCountIntegrateTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupHashtagRepository groupHashtagRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Group group;
    User user;

    @BeforeEach
    public void init() {
        String rawPassword = "123qwe!@#";
        user = User.builder()
            .email("view@aaa.aaa")
            .password(passwordEncoder.encode(rawPassword))
            .nickname("view")
            .status(UserStatus.ACTIVE)
            .role(Role.ROLE_USER)
            .build();

        userRepository.save(user);

        group = Group.builder()
            .leader(user)
            .title("테스트1")
            .explain("테스트 모임 설명")
            .imageUrl("...")
            .simpleExplain("간단 설명")
            .placeName("최고집손짜장")
            .address("우리집앞")
            .groupDate(LocalDateTime.now().plusDays(1))
            .viewCount(0)
            .maxPeople(5)
            .nowPeople(2)
            .status(GroupStatus.RECRUITING)
            .latitude(37.5665)
            .longitude(126.9780)
            .during(120)
            .category(GroupClassification.STUDY)
            .build();

        groupRepository.save(group);

        List<String> tags = List.of("hashtag1", "hashtag2", "hashtag3");
        List<GroupHashtag> hashtags = tags.stream()
            .map(tag -> GroupHashtag.builder()
                .tag(tag)
                .group(group)
                .build())
            .collect(Collectors.toList());

        group.setHashtags(hashtags);
        groupHashtagRepository.saveAll(hashtags);

    }

    @Test
    public void viewCount() {
        Long groupId = group.getId();
        String email = user.getEmail();

        String viewCountKey = "group:" + groupId + ":viewCount";
        String userKey = "group:" + groupId + ":user:" + email;

        //조회수 키 없는지 확인
        assertThat(redisTemplate.hasKey(viewCountKey)).isFalse();
        assertThat(redisTemplate.hasKey(userKey)).isFalse();

        groupService.get(groupId, email);

        //조회수 키 있는지 확인
        assertTrue(redisTemplate.hasKey(viewCountKey));
        assertTrue(redisTemplate.hasKey(userKey));

        //조회수 확인
        String viewCount = redisTemplate.opsForValue().get(viewCountKey);
        assertEquals("1", viewCount);
    }
}

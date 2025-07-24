package com.grepp.funfun.app.domain.integrate;

import com.grepp.funfun.app.domain.group.dto.payload.GroupListResponse;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.group.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class GroupSearchIntegrateTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void SearchAllTest() {
        // 시간 측정 시작
        long startTime = System.nanoTime();

        String keyword = "화났다";
        Page<GroupListResponse> result = groupService.getGroups(null, keyword, "recent", null,
            Pageable.ofSize(16));

        // 시간 측정 끝
        long endTime = System.nanoTime();

        // 실행 시간 출력 (밀리초 단위로 보기 좋게 변환)
        long durationInMillis = (endTime - startTime) / 1_000_000;
        log.info("{} 검색 쿼리 실행 시간 : {} ms", keyword, durationInMillis);
        log.info("{} 쿼리 실행 결과 크기 : {}", keyword, result.getSize());
    }

    // 1,000 건 - page size 16
    // 전체 조회 쿼리 실행 시간 : 212 ms
    // e스포츠 검색 쿼리 실행 시간 : 423 ms
    // 테니스 검색 쿼리 실행 시간 : 470 ms

    // 11,000 건 - page size 16
    // 전체 조회 쿼리 실행 시간 : 249 ms
    // e스포츠 검색 쿼리 실행 시간 : 628 ms
    // 테니스 검색 쿼리 실행 시간 : 522 ms

    // 1,000,000 건 - page size 16
    // 전체 조회 쿼리 실행 시간 : 1354 ms
    // e스포츠 검색 쿼리 실행 시간 : 3296 ms
    // 테니스 검색 쿼리 실행 시간 : 3421 ms
    // 독립영화 검색 쿼리 실행 시간 : 3756 ms
    // 화났다 검색 쿼리 실행 시간 : 2066 ms
}

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
    void searchAllTest_LIKE() {
        // 시간 측정 시작
        long startTime = System.nanoTime();

        String keyword = "클래스";
        Page<GroupListResponse> result = groupService.getGroups(null, keyword, "recent", null,
            Pageable.ofSize(16));

        // 시간 측정 끝
        long endTime = System.nanoTime();

        // 실행 시간 출력 (밀리초 단위로 보기 좋게 변환)
        long durationInMillis = (endTime - startTime) / 1_000_000;
        log.info("{} 검색 쿼리 실행 시간 : {} ms", keyword, durationInMillis);
        log.info("{} 쿼리 실행 결과 크기 : {}", keyword, result.getContent().size());
        log.info("{} 쿼리 실행 결과 총 데이터 개수 : {}", keyword, result.getTotalElements());

        // 1,000 건 - page size 16
        // 전체 조회 쿼리 실행 시간 : 270 ms
        // 로맨스 검색 쿼리 실행 시간 : 488 ms - 62개
        // 독립영화 검색 쿼리 실행 시간 : 364 ms - 48개
        // 클래스 검색 쿼리 실행 시간 : 382 ms - 176개

        // 10,000 건 - page size 16
        // 전체 조회 쿼리 실행 시간 : 199 ms
        // 로맨스 검색 쿼리 실행 시간 : 407 ms - 139개
        // 독립영화 검색 쿼리 실행 시간 : 345 ms - 109개
        // 클래스 검색 쿼리 실행 시간 : 353 ms - 1778개

        // 100,000 건 - page size 16
        // 전체 조회 쿼리 실행 시간 : 365 ms
        // 로맨스 검색 쿼리 실행 시간 : 475 ms - 746개
        // 독립영화 검색 쿼리 실행 시간 : 458 ms - 587개
        // 클래스 검색 쿼리 실행 시간 : 520 ms - 17903개

        // 1,000,000 건 - page size 16
        // 전체 조회 쿼리 실행 시간 : 1585 ms
        // 로맨스 검색 쿼리 실행 시간 : 2617 ms - 7465개
        // 독립영화 검색 쿼리 실행 시간 : 2772 ms - 5841개
        // 클래스 검색 쿼리 실행 시간 : 2563 ms - 179088개

        // 1,000,000 건 - page size 16 - yunseo's computer
        // 독립영화 검색 쿼리 실행 시간 : 12455 ms
        // 로맨스 검색 쿼리 실행 시간 : 12451 ms
    }

    @Test
    void searchAllTest_ES() {
        // 시간 측정 시작
        long startTime = System.nanoTime();

        String keyword = "클래스";
        Page<GroupListResponse> result = groupService.searchGroups(keyword, null, Pageable.ofSize(16));

        // 시간 측정 끝
        long endTime = System.nanoTime();

        // 실행 시간 출력 (밀리초 단위로 보기 좋게 변환)
        long durationInMillis = (endTime - startTime) / 1_000_000;
        log.info("{} 검색 쿼리 실행 시간 : {} ms", keyword, durationInMillis);
        log.info("{} 쿼리 실행 결과 크기 : {}", keyword, result.getContent().size());
        log.info("{} 쿼리 실행 결과 총 데이터 개수 : {}", keyword, result.getTotalElements());
//        log.info("{} 쿼리 실행 결과 데이터 : {}", keyword, result.getContent());

        // 1,000 건 - page size 16
        // 전체 조회 쿼리 실행 시간 : 219 ms
        // 로맨스 검색 쿼리 실행 시간 : 181 ms - 62개
        // 독립영화 검색 쿼리 실행 시간 : 195 ms - 294개
        // 클래스 검색 쿼리 실행 시간 : 154 ms - 176개

        // 10,000 건 - page size 16
        // 전체 조회 쿼리 실행 시간 : 186 ms
        // 로맨스 검색 쿼리 실행 시간 : 179 ms - 139개
        // 독립영화 검색 쿼리 실행 시간 : 213 ms - 705개
        // 클래스 검색 쿼리 실행 시간 : 168 ms - 1896개

        // 100,000 건 - page size 16
        // 전체 조회 쿼리 실행 시간 : 193 ms
        // 로맨스 검색 쿼리 실행 시간 : 180 ms - 746개
        // 독립영화 검색 쿼리 실행 시간 : 197 ms - 3907개
        // 클래스 검색 쿼리 실행 시간 : 213 ms - 18536개

        // 1,000,000 건 - page size 16
        // 전체 조회 쿼리 실행 시간 : 219 ms
        // 로맨스 검색 쿼리 실행 시간 : 329 ms - 7465개
        // 독립영화 검색 쿼리 실행 시간 : 265 ms - 38999개
        // 클래스 검색 쿼리 실행 시간 : 307 ms - 185422개
    }


}

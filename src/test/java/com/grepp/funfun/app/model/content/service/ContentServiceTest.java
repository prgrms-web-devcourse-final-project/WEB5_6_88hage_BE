package com.grepp.funfun.app.model.content.service;

import com.grepp.funfun.app.controller.api.content.payload.ContentFilterRequest;
import com.grepp.funfun.app.model.content.code.ContentClassification;
import com.grepp.funfun.app.model.content.dto.ContentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ContentServiceTest {

    @Autowired
    private ContentService contentService;

    @Test
    public void getContents(){

        ContentFilterRequest request = new ContentFilterRequest();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ContentDTO> result = contentService.findByFilters(request, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        System.out.println("========== 전체 조회 ===========");
        System.out.println("총 개수: " + result.getTotalElements());
        result.getContent().forEach(content ->
                System.out.println(content.getId() + " - " + content.getContentTitle())
        );

    }

    @Test
    public void getFilterByCategory(){
        ContentFilterRequest request = new ContentFilterRequest();
        request.setCategory(ContentClassification.DANCE);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ContentDTO> result = contentService.findByFilters(request, pageable);

        // then
        assertThat(result).isNotNull();

        System.out.println("============= 무용 카테고리 필터링 ============");
        result.getContent().forEach(content ->
                System.out.println(content.getContentTitle() + " - 카테고리: " + content.getCategory())
        );
    }

    @Test
    public void getFilterByGuName() {
        // given
        ContentFilterRequest request = new ContentFilterRequest();
        request.setGuName("강남구");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ContentDTO> result = contentService.findByFilters(request, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).allMatch(content ->
                "강남구".equals(content.getGuName())
        );

        System.out.println("======= 강남구 지역 필터링 =======");
        result.getContent().forEach(content ->
                System.out.println(content.getContentTitle() + " - 지역: " + content.getGuName())
        );
    }

    @Test
    public void getContentsByFilters() {
        // given
        ContentFilterRequest request = new ContentFilterRequest();
        request.setCategory(ContentClassification.DANCE);
        request.setGuName("강남구");
        request.setStartDate(LocalDate.of(2025, 7, 1));
        request.setEndDate(LocalDate.of(2025, 12, 31));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bookmarkCount").descending());

        // when
        Page<ContentDTO> result = contentService.findByFilters(request, pageable);

        // then
        assertThat(result).isNotNull();

        System.out.println("=== 복합 필터링 (무용 + 강남구 + 2025년 하반기 + 북마크순) ===");
        result.getContent().forEach(content ->
                System.out.println(content.getContentTitle() +
                        " - 카테고리: " + content.getCategory() +
                        ", 지역: " + content.getGuName() +
                        ", 기간: " + content.getStartDate() + "~" + content.getEndDate() +
                        ", 북마크: " + content.getBookmarkCount())
        );
    }

}
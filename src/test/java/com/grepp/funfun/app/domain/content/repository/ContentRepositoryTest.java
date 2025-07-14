package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.entity.Content;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
class ContentRepositoryTest {

    @Autowired
    private ContentRepository contentRepository;

    @Test
    void 북마크순_정렬_테스트() {
        Sort sort = Sort.by(Sort.Direction.DESC, "bookmarkCount");
        List<Content> result = contentRepository.findAll(sort);

        assertThat(result).isNotEmpty();

        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getBookmarkCount())
                    .isGreaterThanOrEqualTo(result.get(i + 1).getBookmarkCount());
        }

        System.out.println("=== 북마크순 정렬 결과 ===");
        result.forEach(content ->
                System.out.println(content.getContentTitle() + " - 북마크: " + content.getBookmarkCount())
        );
    }

    @Test
    void 개최임박순_정렬_테스트() {
        Sort sort = Sort.by(Sort.Direction.ASC, "startDate");
        List<Content> result = contentRepository.findAll(sort);

        assertThat(result).isNotEmpty();

        for (int i = 0; i < result.size() - 1; i++) {
            Content current = result.get(i);
            Content next = result.get(i + 1);

            if (current.getStartDate() != null && next.getStartDate() != null) {
                assertThat(current.getStartDate())
                        .isBeforeOrEqualTo(next.getStartDate());
            }
        }

        System.out.println("=== 개최 임박순 정렬 결과 ===");
        result.forEach(content ->
                System.out.println(content.getContentTitle() + " - 시작일: " + content.getStartDate())
        );
    }
}
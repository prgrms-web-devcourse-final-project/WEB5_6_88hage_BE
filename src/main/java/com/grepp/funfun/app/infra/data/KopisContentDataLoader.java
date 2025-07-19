package com.grepp.funfun.app.infra.data;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class KopisContentDataLoader {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ContentDTO> fetchContents() {
        // 외부 API 요청 및 JSON 파싱

        // 응답 받아서 DTO 리스트로 매핑
        return List.of(); // TODO: 파싱 구현
    }
}

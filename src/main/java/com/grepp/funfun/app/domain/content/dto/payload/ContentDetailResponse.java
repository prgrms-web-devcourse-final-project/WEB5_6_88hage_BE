package com.grepp.funfun.app.domain.content.dto.payload;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ContentDetailResponse {

    private ContentDTO content;

    private List<ContentDTO> related;

    private List<ContentDTO> nearby;
}

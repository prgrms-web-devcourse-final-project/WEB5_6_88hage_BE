package com.grepp.funfun.app.domain.content.dto.payload;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.ContentSimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ContentDetailResponse {

    private ContentDTO content;

    private List<ContentSimpleDTO> related;

    private List<ContentSimpleDTO> nearby;
}

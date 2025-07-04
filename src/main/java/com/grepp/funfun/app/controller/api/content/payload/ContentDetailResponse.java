package com.grepp.funfun.app.controller.api.content.payload;

import com.grepp.funfun.app.model.content.dto.ContentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ContentDetailResponse {

    private ContentDTO content;

    private List<ContentDTO> related;

    private List<ContentDTO> nearby;
}

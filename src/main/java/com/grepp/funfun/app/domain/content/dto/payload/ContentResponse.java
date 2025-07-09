package com.grepp.funfun.app.domain.content.dto.payload;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.infra.response.PageResponse;

import java.util.List;

public class ContentResponse {

    private List<ContentDTO> contents;
    private PageResponse page;
}

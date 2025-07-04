package com.grepp.funfun.app.controller.api.content.payload;

import com.grepp.funfun.app.model.content.dto.ContentDTO;
import com.grepp.funfun.infra.response.PageResponse;

import java.util.List;

public class ContentResponse {

    private List<ContentDTO> contents;
    private PageResponse page;
}

package com.grepp.funfun.app.domain.recommend.dto.payload;


import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentRecommendResponse {

    private List<ContentDTO> content;

}

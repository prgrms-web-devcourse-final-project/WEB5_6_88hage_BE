package com.grepp.funfun.app.domain.group.controller;

import com.grepp.funfun.app.domain.group.service.GroupHashtagService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/groupHashtags", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupHashtagApiController {

    private final GroupHashtagService groupHashtagService;

    @PostMapping("/save")
    @Operation(summary = "자동 완성을 위한 단어 저장", description = "자동 완성을 위한 단어 저장합니다.(모임 해시태그)")
    public ResponseEntity<ApiResponse<String>> saveWord(@RequestParam String keyword){
        groupHashtagService.saveWord(keyword);
        return ResponseEntity.ok(com.grepp.funfun.app.infra.response.ApiResponse.success("단어 저장 완료"));
    }

    @GetMapping("/complete")
    @Operation(summary = "자동 완성을 위한 단어 불러오기", description = "자동 완성을 위한 단어를 redis 에서 불러옵니다.(모임 해시태그)")
    public ResponseEntity<ApiResponse<Set<String>>> completeWord(@RequestParam String prefix){
        Set<String> Words = groupHashtagService.getAutoCompleteWord(prefix);
        return ResponseEntity.ok(com.grepp.funfun.app.infra.response.ApiResponse.success(Words));
    }

}

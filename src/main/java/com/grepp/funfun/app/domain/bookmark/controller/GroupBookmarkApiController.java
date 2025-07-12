package com.grepp.funfun.app.domain.bookmark.controller;

import com.grepp.funfun.app.domain.bookmark.dto.GroupBookmarkDTO;
import com.grepp.funfun.app.domain.bookmark.dto.payload.GroupBookmarkResponse;
import com.grepp.funfun.app.domain.bookmark.service.GroupBookmarkService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/groupBookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupBookmarkApiController {

    private final GroupBookmarkService groupBookmarkService;

    // 즐겨찾기 추가(모임)
    @PostMapping("/{groupId}")
    @Operation(summary = "즐겨찾기 추가(모임)", description = "선택한 모임을 즐겨찾기에 추가합니다.")
    public ResponseEntity<ApiResponse<String>> groupBookmarkAdd(@PathVariable Long groupId,
        Authentication authentication) {
        String userEmail = authentication.getName();

        groupBookmarkService.addGroupBookmark(groupId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Added group bookmark"));
    }

    // 즐겨찾기 삭제(모임)
    @DeleteMapping("/{groupId}")
    @Operation(summary = "즐겨찾기 삭제(모임)", description = "선택한 모임을 즐겨찾기에서 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> groupBookmarkDelete(@PathVariable Long groupId,
        Authentication authentication) {
        String userEmail = authentication.getName();

        groupBookmarkService.removeGroupBookmark(groupId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Deleted group bookmark"));
    }


    // 즐겨찾기 모임 조회
    @GetMapping("/getBookMarks")
    @Operation(summary = "내 즐겨찾기 조회", description = "내가 즐겨찾기한 모임 내용을 조회합니다.")
    public ResponseEntity<ApiResponse<List<GroupBookmarkResponse>>> getBookMarks(Authentication authentication) {
        String userEmail = authentication.getName();

        List<GroupBookmarkResponse> myBookMark = groupBookmarkService.getMyGroupBookMarks(userEmail);

        return ResponseEntity.ok(ApiResponse.success(myBookMark));
    }

    @GetMapping
    public ResponseEntity<List<GroupBookmarkDTO>> getAllGroupBookmarks() {
        return ResponseEntity.ok(groupBookmarkService.findAll());
    }


}

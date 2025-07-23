package com.grepp.funfun.app.domain.chat.controller;

import com.grepp.funfun.app.domain.chat.dto.payload.ChatResponse;
import com.grepp.funfun.app.domain.chat.service.ChatService;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/chats", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ChatApiController {

    private final ChatService chatService;

    @GetMapping("/{roomId}/{type}/history")
    @Operation(summary = "채팅 기록 조회", description = "팀의 이전 채팅 내용을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getChatHistory(@PathVariable Long roomId,
        @PathVariable ChatRoomType type) {
        try {
            if (roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
            }
            List<ChatResponse> chatHistory = chatService.getChatHistory(roomId, type);
            return ResponseEntity.ok(ApiResponse.success(chatHistory));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping("/{roomId}/{type}/lastHistory")
    @Operation(summary = "마지막 채팅 조회", description = "모임의 마지막 채팅을 조회합니다.")
    public ResponseEntity<ApiResponse<Optional<ChatResponse>>> getLastHistory(
        @PathVariable Long roomId,
        @PathVariable ChatRoomType type) {
        try {
            if (roomId == null || roomId <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
            }
            Optional<ChatResponse> chatLastHistory = chatService.getLastChatHistory(roomId, type);

            return ResponseEntity.ok(ApiResponse.success(chatLastHistory));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
        }
    }

}

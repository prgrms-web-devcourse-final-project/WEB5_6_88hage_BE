package com.grepp.funfun.app.domain.chat.controller;

import com.grepp.funfun.app.domain.chat.dto.payload.PersonalChatRoomResponse;
import com.grepp.funfun.app.domain.chat.entity.PersonalChatRoom;
import com.grepp.funfun.app.domain.chat.repository.PersonalChatRoomRepository;
import com.grepp.funfun.app.domain.chat.service.ChatRoomService;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/chatRooms", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;
    private final PersonalChatRoomRepository personalChatRoomRepository;

    // 개인 채팅방 생성
    @PostMapping("/personalRooms")
    @Operation(summary = "개인 채팅방 생성", description = "사용자간 1:1 채팅방을 생성합니다.")
    public ResponseEntity<ApiResponse<String>> createPersonalChatRoom(
        @RequestParam String targetUserEmail,
        Authentication authentication) {

        String userEmail = authentication.getName();

        chatRoomService.createPersonalChatRoom(userEmail, targetUserEmail);

        return ResponseEntity.ok(ApiResponse.success("채팅방 생성이 완료되었습니다."));
    }

    // 내 개인 채팅방 목록 조회
    @GetMapping("/my")
    @Operation(summary = "개인 채팅방 조회", description = "로그인 한 사용자의 개인 채팅방을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PersonalChatRoomResponse>>> getMyPersonalChatRooms(
        Authentication authentication) {

        String userEmail = authentication.getName();

        List<PersonalChatRoomResponse> chatRooms = chatRoomService.getMyPersonalChatRooms(userEmail);

        return ResponseEntity.ok(ApiResponse.success(chatRooms));
    }

    @PostMapping("/{roomId}/leave")
    @Operation(summary = "개인 채팅방 나가기", description = "개인 채팅방을 나갑니다.")
    public ResponseEntity<ApiResponse<String>> getLastHistory(
        @PathVariable Long roomId,
        Authentication authentication) {

        String userEmail = authentication.getName();

        PersonalChatRoom chatRoom = personalChatRoomRepository.findById(roomId)
            .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST, "채팅방이 존재하지 않습니다."));

        String targetEmail;
        if (chatRoom.getUserAEmail().equals(userEmail)) {
            targetEmail = chatRoom.getUserBEmail();
        } else if (chatRoom.getUserBEmail().equals(userEmail)) {
            targetEmail = chatRoom.getUserAEmail();
        } else {
            throw new CommonException(ResponseCode.BAD_REQUEST, "사용자가 해당 채팅방에 속해있지 않습니다.");
        }

        chatRoomService.deletePersonalChatRoom(userEmail, targetEmail);

        return ResponseEntity.ok(ApiResponse.success("채팅방 나가기를 완료하였습니다."));

    }
}

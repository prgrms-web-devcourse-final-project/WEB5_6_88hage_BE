package com.grepp.funfun.app.controller.api.chat;

import com.grepp.funfun.app.controller.api.chat.payload.ChatResponse;
import com.grepp.funfun.app.model.chat.dto.ChatDTO;
import com.grepp.funfun.app.model.chat.service.ChatService;
import com.grepp.funfun.infra.response.ApiResponse;
import com.grepp.funfun.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/chats", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ChatApiController {

    private final ChatService chatService;

    @GetMapping("/history/{roomId}")
    @Operation(summary = "채팅 기록 조회", description = "팀의 이전 채팅 내용을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getChatHistory(@PathVariable Long roomId) {
        try{
            if(roomId == null || roomId <= 0){
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
            }
            List<ChatResponse> chatHistory = chatService.getChatHistory(roomId);
            return ResponseEntity.ok(ApiResponse.success(chatHistory));
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
        }catch(Exception e){
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
        }

    }

    @GetMapping
    public ResponseEntity<List<ChatDTO>> getAllChats() {
        return ResponseEntity.ok(chatService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatDTO> getChat(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(chatService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createChat(@RequestBody @Valid final ChatDTO chatDTO) {
        final Long createdId = chatService.create(chatDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChat(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ChatDTO chatDTO) {
        chatService.update(id, chatDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable(name = "id") final Long id) {
        chatService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

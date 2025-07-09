package com.grepp.funfun.app.domain.chat.controller;

import com.grepp.funfun.app.domain.chat.dto.ChatRoomDTO;
import com.grepp.funfun.app.domain.chat.service.ChatRoomService;
import com.grepp.funfun.app.delete.util.ReferencedException;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping(value = "/api/chatRooms", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;

    public ChatRoomApiController(final ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomDTO>> getAllChatRooms() {
        return ResponseEntity.ok(chatRoomService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(chatRoomService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createChatRoom(@RequestBody @Valid final ChatRoomDTO chatRoomDTO) {
        final Long createdId = chatRoomService.create(chatRoomDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChatRoom(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ChatRoomDTO chatRoomDTO) {
        chatRoomService.update(id, chatRoomDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = chatRoomService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        chatRoomService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

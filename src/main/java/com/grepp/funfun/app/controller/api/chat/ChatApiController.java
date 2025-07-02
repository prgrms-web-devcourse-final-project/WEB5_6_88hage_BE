package com.grepp.funfun.app.controller.api.chat;

import com.grepp.funfun.app.model.chat.dto.ChatDTO;
import com.grepp.funfun.app.model.chat.service.ChatService;
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
@RequestMapping(value = "/api/chats", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatApiController {

    private final ChatService chatService;

    public ChatApiController(final ChatService chatService) {
        this.chatService = chatService;
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
    @ApiResponse(responseCode = "201")
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
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChat(@PathVariable(name = "id") final Long id) {
        chatService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

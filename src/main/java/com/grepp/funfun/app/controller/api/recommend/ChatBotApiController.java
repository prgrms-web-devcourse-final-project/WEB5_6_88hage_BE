package com.grepp.funfun.app.controller.api.recommend;

import com.grepp.funfun.app.model.recommend.dto.ChatBotDTO;
import com.grepp.funfun.app.model.recommend.service.ChatBotService;
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
@RequestMapping(value = "/api/chatBots", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatBotApiController {

    private final ChatBotService chatBotService;

    public ChatBotApiController(final ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @GetMapping
    public ResponseEntity<List<ChatBotDTO>> getAllChatBots() {
        return ResponseEntity.ok(chatBotService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatBotDTO> getChatBot(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(chatBotService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createChatBot(@RequestBody @Valid final ChatBotDTO chatBotDTO) {
        final Long createdId = chatBotService.create(chatBotDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChatBot(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ChatBotDTO chatBotDTO) {
        chatBotService.update(id, chatBotDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChatBot(@PathVariable(name = "id") final Long id) {
        chatBotService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

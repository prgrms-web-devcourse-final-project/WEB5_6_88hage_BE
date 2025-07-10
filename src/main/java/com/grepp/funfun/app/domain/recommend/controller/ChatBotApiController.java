package com.grepp.funfun.app.domain.recommend.controller;

import com.grepp.funfun.app.domain.content.service.ContentAiService;
import com.grepp.funfun.app.domain.recommend.dto.ChatBotDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendDTO;
import com.grepp.funfun.app.domain.recommend.dto.payload.ContentRecommendResponse;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendRequest;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendResponse;
import com.grepp.funfun.app.domain.recommend.service.ChatBotService;
import com.grepp.funfun.app.domain.recommend.vo.EventType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/chatBots", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatBotApiController {

    private final ChatBotService chatBotService;
    private final ContentAiService contentsAiService;

    @PostMapping("chat")
    public String chat(@RequestBody String message){
        return contentsAiService.chat(message);
    }

    @PostMapping("content")
    public ResponseEntity<ContentRecommendResponse> chat(@RequestBody RecommendRequest request){
        if(request.getEventType() == EventType.CONTENT){
            String prompt = request.getStartTime() + "부터 " + request.getEndTime() + "까지 여가시간인데 이 때 할만한"
                + "활동을 추천해줘. 장소, 시간 조건에 맞게 실제로 실행할 수 있는 활동들을 2개만 추천해줘";
            RecommendResponse contents = contentsAiService.recommend(prompt);

            List<RecommendDTO> recommendList = contents.recommend();

            log.info(" 추천 결과 ==================");
            for (RecommendDTO dto : recommendList) {
                log.info("id: {}", dto.id());
                log.info("title: {}", dto.contentTitle());
            }
            return ResponseEntity.ok(null);

        } else if(request.getEventType() == EventType.GROUP){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
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

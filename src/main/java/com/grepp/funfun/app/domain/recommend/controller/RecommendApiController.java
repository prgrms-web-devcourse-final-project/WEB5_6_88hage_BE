package com.grepp.funfun.app.domain.recommend.controller;

import com.grepp.funfun.app.domain.content.service.ContentService;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.domain.recommend.dto.payload.ChatBotRequest;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendContentResponse;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendGroupResponse;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendRequest;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendTwoListResponse;
import com.grepp.funfun.app.domain.recommend.service.AiRequestQueue;
import com.grepp.funfun.app.domain.recommend.service.ChatBotAiService;
import com.grepp.funfun.app.domain.recommend.service.ChatBotService;
import com.grepp.funfun.app.domain.recommend.service.DynamicService;
import com.grepp.funfun.app.domain.user.service.UserService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import dev.langchain4j.service.output.OutputParsingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.SocketTimeoutException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 추천 API", description = "추천과 관련된 기능들입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecommendApiController {

    private final ChatBotService chatBotService;

    @PostMapping("chatBot/chat")
    @Operation(summary = "챗봇 대화 기능", description = "챗봇과 대화하여 사용자의 취향을 분석함")
    public ResponseEntity<ApiResponse<String>> chat(
        @RequestBody ChatBotRequest request) {

        CompletableFuture<ApiResponse<String>> future = chatBotService.chatBotConversation(request);
        ResponseEntity<ApiResponse<String>> result = future.thenApply(ResponseEntity::ok).join();
        return result;
    }

    @PostMapping("chatBot/end")
    @Operation(summary = "챗봇 대화내용 요약 기능", description = "챗봇과의 대화내용을 바탕으로 사용자의 취향을 분석하여 저장")
    public ResponseEntity<ApiResponse<String>> chatBotCloseAndSummary(
        @RequestBody ChatBotRequest request, Authentication authentication
    ) {
        CompletableFuture<ApiResponse<String>> future = chatBotService.chatBotCloseAndSummary(request, authentication);
        ResponseEntity<ApiResponse<String>> result = future.thenApply(ResponseEntity::ok)
            .exceptionally(e -> {
                log.error("요약 처리 중 오류 발생", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,
                                                             "챗봇 요약 중 오류 발생"));
            }).join();

        return result;
    }

    @PostMapping("chatBot/recommend/content")
    @Operation(summary = "챗봇 추천 기능 (컨텐츠)", description = "시간, 장소를 입력하여 추천을 받습니다.")
    public ResponseEntity<ApiResponse<RecommendContentResponse>> chatBotRecommendContent(
        @Valid @RequestBody RecommendRequest request, Authentication authentication
    ) {
        CompletableFuture<RecommendContentResponse> future = chatBotService.chatBotRecommendContent(request, authentication);
        ResponseEntity<ApiResponse<RecommendContentResponse>> result = future.thenApply(response ->
                                                                                       ResponseEntity.ok(ApiResponse.success(response))).exceptionally(e -> {
            log.error("API 호출 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,
                                                         "추천 처리 중 오류가 발생했습니다."));
        }).join();

        return result;
    }

    @PostMapping("chatBot/recommend/group")
    @Operation(summary = "챗봇 추천 기능 (모임)", description = "시간, 장소를 입력하여 추천을 받습니다.")
    public ResponseEntity<ApiResponse<RecommendGroupResponse>> chatBotRecommendGroup(
        @Valid @RequestBody RecommendRequest request,
        Authentication authentication
    ) {
        CompletableFuture<RecommendGroupResponse> future = chatBotService.chatBotRecommendGroup(request, authentication);
        ResponseEntity<ApiResponse<RecommendGroupResponse>> result = future.thenApply(response ->
                                                                                     ResponseEntity.ok(ApiResponse.success(response))).exceptionally(e -> {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            Throwable cause = e.getCause();
            if (cause instanceof OutputParsingException) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                     .body(ApiResponse.error(ResponseCode.INVALID_API_RESPONSE,
                                                             "AI 응답이 길어서 Json 파싱에서 문제 발생"));
            } else if (cause instanceof SocketTimeoutException) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                                     .body(ApiResponse.error(ResponseCode.API_UNAVAILABLE,
                                                             "AI 서버 응답이 지연되고 있습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,
                                                             "요청 처리 중 알 수 없는 오류가 발생했습니다."));
            }
        }).join();

        return result;
    }

    @PostMapping("recommend/content")
    @Operation(summary = "AI 빠른추천 기능 (컨텐츠)", description = "시간, 장소를 입력하여 추천을 받습니다.")
    public ResponseEntity<ApiResponse<RecommendTwoListResponse>> quickRecommendContent(
        @Valid @RequestBody RecommendRequest request,
        Authentication authentication
    ) {
        CompletableFuture<RecommendTwoListResponse> future = chatBotService.quickRecommendContent(request, authentication);
        ResponseEntity<ApiResponse<RecommendTwoListResponse>> result = future.thenApply(response ->
                                                                                     ResponseEntity.ok(ApiResponse.success(response))).exceptionally(e -> {
                                 log.error("API 호출 중 오류 발생: " + e.getMessage(), e);
                                 Throwable cause = e.getCause();
                                 if (cause instanceof OutputParsingException) {
                                     return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                                          .body(ApiResponse.error(ResponseCode.INVALID_API_RESPONSE,
                                                                                  "AI 응답이 길어서 Json 파싱에서 문제 발생"));
                                 } else if (cause instanceof SocketTimeoutException) {
                                     return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                                                          .body(ApiResponse.error(ResponseCode.API_UNAVAILABLE,
                                                                                  "AI 서버 응답이 지연되고 있습니다."));
                                 } else {
                                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                          .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,
                                                                                  "요청 처리 중 알 수 없는 오류가 발생했습니다."));
                                 }
                             }).join();

        return result;
    }

    @PostMapping("recommend/group")
    @Operation(summary = "AI 빠른추천 기능 (모임)", description = "시간, 장소를 입력하여 추천을 받습니다.")
    public ResponseEntity<ApiResponse<RecommendGroupResponse>> quickRecommendGroup(
        @Valid @RequestBody RecommendRequest request,
        Authentication authentication) {

        CompletableFuture<RecommendGroupResponse> future = chatBotService.quickRecommendGroup(request, authentication);
        ResponseEntity<ApiResponse<RecommendGroupResponse>> result = future.thenApply(response ->
                                                        ResponseEntity.ok(ApiResponse.success(response))

        ).exceptionally(e -> {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            Throwable cause = e.getCause();
            if (cause instanceof OutputParsingException) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                                     .body(ApiResponse.error(ResponseCode.INVALID_API_RESPONSE,
                                                             "AI 응답이 길어서 Json 파싱에서 문제 발생"));
            } else if (cause instanceof SocketTimeoutException) {
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                                     .body(ApiResponse.error(ResponseCode.API_UNAVAILABLE,
                                                             "AI 서버 응답이 지연되고 있습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,
                                                             "요청 처리 중 알 수 없는 오류가 발생했습니다."));
            }
        }).join();

        return result;
    }
}

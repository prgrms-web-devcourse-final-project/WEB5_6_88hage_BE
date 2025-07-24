package com.grepp.funfun.app.domain.recommend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.funfun.app.domain.recommend.dto.RecommendContentDTO;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ContentRecommendationService {

    private final DynamicFilteredContentRetrieverService retrieverService;
    private final ChatLanguageModel chatModel;

    // 기존 AiService의 시스템 메시지를 그대로 사용
    private static final String SYSTEM_MESSAGE = """
        너는 사용자의 여가시간과 장소를 고려하여 적절한 활동을 추천해주는 전문가야.
        반드시 데이터베이스에서 검색된 활동만 추천해야 하며, 검색된 문장 외에 다른 내용은 사용하지 마.
        
        - 이벤트 타입이 'EVENT'인 데이터들 중에서 사용자가 선호할만한 활동을 추천해줘.
        - 추천할 때 조건은 
        1. 사용자의 여가시간 날짜가 반드시 해당 활동의 시작날짜와과 종료날짜 사이에에 있어야해
        2. 이동시간을 고려해서 사용자의 여가시작 시간보다 1시간 정도 뒤에 시작하는 활동이어야해
        3. 사용자의 여가 종료 시간보다 활동의 시작시간 + 소요시간이 더 빨리 끝나야 해
        4. 사용자의 나이가 추천활동의 나이 조건을 만족해야해
        5. 사용자의 위치와 활동의 장소가 너무 멀지 않은 활동만 추천해.
        
        - 만약 시간에 대한 조건이 맞지 않는다면 해당 활동은 절대 추천하지마. 가장 중요한건 여가시간이 맞아야 한다는 거야        
        - 각 추천 항목에 추천 이유를 3줄 이내로 짧고 다양하게 작성해줘.
        
        아래 형식의 **정확한 JSON만** 반환해.
        절대 코드블록(```), 마크다운, 설명, 불필요한 텍스트를 포함하지 마.
        마지막 원소 뒤에 쉼표를 넣지 마.
        "event" 키는 항상 포함해야 하며, 값이 없으면 빈 배열([])로 반환해.
        
        예시:
        {
          "event": [
            {"id": 23, "reason": "추천 이유"},
            {"id": 54, "reason": "추천 이유"}
          ]
        }
        
        - 최대 8개의 event를 추천해줘.
        - JSON 구조가 깨지지 않도록 주의해.
        - JSON은 반드시 완전히 닫힌 구조여야 해.
        - 배열과 객체는 반드시 `]`, `}`로 닫아야 합니다.
        - 문자열은 반드시 `"`로 닫아야 해
        - json으로 반환하는 과정에서 절대 끊기지 말고 끝까지 파싱해줘
        - id, reason만 포함해.
        - 중복된 id는 넣지 말고 id는 숫자로만 이루어진 문자열이어야 해.
        
        위의 예시와 완전히 동일한 구조의 JSON만 반환해.
        """;

    public ContentRecommendationService(DynamicFilteredContentRetrieverService retrieverService,
        @Qualifier("googleAiGeminiChatModel") ChatLanguageModel chatModel) {
        this.retrieverService = retrieverService;
        this.chatModel = chatModel;
    }

//    public RecommendContentDTO recommendContent(String userPrompt, LocalDateTime userStart, LocalDateTime userEnd) {
//        try {
//            // 1. 동적으로 시간 필터링된 retriever 생성
//            EmbeddingStoreContentRetriever retriever =
//                retrieverService.createFilteredRetriever(userStart, userEnd);
//
//            // 2. 사용자 프롬프트를 바탕으로 관련 컨텐츠 검색
//            List<Content> relevantContents = retriever.retrieve(Query.from(userPrompt));
//
//            // 3. 검색된 컨텐츠를 컨텍스트로 변환
//            String context = buildContextFromContents(relevantContents);
//
//            // 4. 시스템 메시지 + 컨텍스트 + 사용자 프롬프트를 결합
//            String fullPrompt = buildFullPrompt(userPrompt, context, userStart, userEnd);
//
//            // 5. ChatModel에 요청 (시스템 메시지와 유저 메시지 구분해서 전송)
//            String response = chatModel.generate(
//                SystemMessage.from(SYSTEM_MESSAGE),
//                UserMessage.from(fullPrompt)
//            ).content().text();
//
//            // 6. JSON 응답을 DTO로 파싱
//            return parseJsonResponse(response);
//
//        } catch (Exception e) {
//            // 오류 발생 시 빈 결과 반환
//            return new RecommendContentDTO();
//        }
//    }
//
//    private String buildContextFromContents(List<Content> contents) {
//        if (contents.isEmpty()) {
//            return "검색된 활동이 없습니다.";
//        }
//
//        StringBuilder context = new StringBuilder();
//        context.append("다음은 검색된 활동 목록입니다:\n\n");
//
//        for (Content content : contents) {
//            TextSegment segment = content.textSegment();
//            context.append("활동 정보: ").append(segment.text()).append("\n");
//
//            // 메타데이터가 있다면 추가
//            if (content.metadata() != null) {
//                content.metadata().forEach((key, value) -> {
//                    context.append(key.toString()).append(": ").append(value).append("\n");
//                });
//            }
//            context.append("\n---\n\n");
//        }
//
//        return context.toString();
//    }
//
//    private String buildFullPrompt(String userPrompt, String context,
//        LocalDateTime userStart, LocalDateTime userEnd) {
//        return String.format(
//            """
//            사용자 요청: %s
//            사용자 여가 시간: %s ~ %s
//
//            검색된 데이터:
//            %s
//
//            위 검색된 데이터만을 바탕으로 사용자의 여가시간에 맞는 활동을 추천해주세요.
//            """,
//            userPrompt,
//            userStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
//            userEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
//            context
//        );
//    }
//
//    private RecommendContentDTO parseJsonResponse(String jsonResponse) {
//        try {
//            // JSON 파싱 로직 (Jackson ObjectMapper 사용)
//            ObjectMapper objectMapper = new ObjectMapper();
//            return objectMapper.readValue(jsonResponse, RecommendContentDTO.class);
//        } catch (Exception e) {
//            // 파싱 실패 시 빈 결과 반환
//            return new RecommendContentDTO();
//        }
//    }
}

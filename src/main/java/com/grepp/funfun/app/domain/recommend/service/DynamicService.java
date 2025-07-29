package com.grepp.funfun.app.domain.recommend.service;

import com.grepp.funfun.app.domain.group.document.GroupEmbedding;
import com.grepp.funfun.app.domain.recommend.dto.IdTitleReasonContentDTO;
import com.grepp.funfun.app.domain.recommend.dto.IdTitleReasonDTO;
import com.grepp.funfun.app.domain.recommend.dto.IdTitleReasonGroupDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendContentDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendGroupDTO;
import com.grepp.funfun.app.domain.recommend.repository.ContentEmbeddingRepository;
import com.grepp.funfun.app.domain.recommend.repository.GroupEmbeddingRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import java.util.ArrayList;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DynamicService {

    private final ChatLanguageModel chatModel;
//    private final ChatBotService chatBotService;
    private final GroupEmbeddingRepository groupEmbeddingRepository;
    private final ContentEmbeddingRepository contentEmbeddingRepository;

    public DynamicService(
        @Qualifier("googleAiGeminiChatModel") ChatLanguageModel chatModel,
//        ChatBotService chatBotService,
        GroupEmbeddingRepository groupEmbeddingRepository,
        ContentEmbeddingRepository contentEmbeddingRepository) {
        this.chatModel = chatModel;
//        this.chatBotService = chatBotService;
        this.groupEmbeddingRepository = groupEmbeddingRepository;
        this.contentEmbeddingRepository = contentEmbeddingRepository;
    }

    /**
     * 시간 필터링된 동적 AiService를 생성하고 추천 실행 이 방식의 장점: 1. LangChain4j가 자동으로 RAG 처리 (토큰 관리 포함) 2.
     * ContentRetriever가 자동으로 DB 연결 3. 프롬프트 토큰 제한 자동 처리
     */
    public RecommendContentDTO chatBotRecommendContent(String userPrompt, Long userStart,
        Long userEnd, EmbeddingStoreContentRetriever filteredRetriever) {
        try {
            log.info("=================== 동적 AiService 생성 시작 ===================");
            log.info("사용자 프롬프트: {}", userPrompt);
            log.info("사용자 여가 시간: {} ~ {}", userStart, userEnd);



            // 동적으로 AiService 생성
            // AiService가 자동으로 RAG 수행하고 추천 실행
            // 이때 ContentRetriever가 자동으로
            // 필터링된 DB에서 검색
            ContentAiService dynamicAiService = AiServices.builder(ContentAiService.class)
                                                          .chatLanguageModel(chatModel)
                                                          .contentRetriever(filteredRetriever)  // 필터링된 retriever 주입
                                                          .build();
            log.info("동적 AiService 생성 완료");

            IdTitleReasonContentDTO mongoResult = dynamicAiService.chatBotRecommendContent(
                userPrompt);

            log.info("모임 추천 결과 ==================");
            for (IdTitleReasonDTO dto : mongoResult.event()) {
                log.info("id: {}", dto.id());
                log.info("title: {}", dto.title());
                log.info("추천이유 : {}", dto.reason());
            }

            RecommendContentDTO result = new RecommendContentDTO(new ArrayList<>());
            for (IdTitleReasonDTO group : mongoResult.event()) {
                Optional<GroupEmbedding> document = contentEmbeddingRepository.findByIdAndTitle(
                    group.id(), group.title());
                log.info("LLM의 추천답변(실존하는지 체크) id: {}, title: {}", group.id(), group.title());
                if (document.isPresent()) {
                    result.event()
                          .add(new RecommendDTO(Long.parseLong(document.get().getId()), group.reason()));
                    log.info("실제 아이디: {}", document.get().getId());
                }
            }

            log.info("추천 완료: {}개 결과", result.event() != null ? result.event().size() : 0);

            return result;

        } catch (Exception e) {
            log.error("동적 AiService 추천 중 오류 발생", e);
            return new RecommendContentDTO(null);
        }
    }

    // inmemoryEmbeddingStore 사용
    public RecommendGroupDTO chatBotRecommendGroup(String prompt, Long userStart, Long userEnd,
        EmbeddingStoreContentRetriever filteredRetriever) {
        try {
            log.info("=================== 동적 AiService 생성 시작 ===================");
            log.info("사용자 프롬프트: {}", prompt);



            GroupAiService dynamicAiService = AiServices.builder(GroupAiService.class)
                                                        .chatLanguageModel(chatModel)
                                                        .contentRetriever(filteredRetriever)
                                                        .build();
            log.info("동적 AiService 생성 완료");

            IdTitleReasonGroupDTO mongoResult = dynamicAiService.chatBotRecommendGroup(prompt);

            log.info("모임 추천 결과 ==================");
            for (IdTitleReasonDTO dto : mongoResult.group()) {
                log.info("id: {}", dto.id());
                log.info("title: {}", dto.title());
                log.info("추천이유 : {}", dto.reason());
            }

            RecommendGroupDTO result = new RecommendGroupDTO(new ArrayList<>());
            for (IdTitleReasonDTO group : mongoResult.group()) {
                Optional<GroupEmbedding> document = groupEmbeddingRepository.findByIdAndTitle(
                    group.id(), group.title());
                log.info("LLM의 추천답변(실존하는지 체크) id: {}, title: {}", group.id(), group.title());
                if (document.isPresent()) {
                    result.group()
                          .add(new RecommendDTO(Long.parseLong(document.get().getId()), group.reason()));
                    log.info("실제 아이디: {}", document.get().getId());
                }
            }
            log.info("추천 완료: {}개 결과",
                     result.group() != null ? result.group()
                                                    .size() : 0);
            return result;

        } catch (Exception e) {
            log.error("동적 AiService 추천 중 오류 발생", e);
            return new RecommendGroupDTO(null);
        }
    }

    public RecommendContentDTO quickRecommendEvent(String userPrompt, Long userStart,
        Long userEnd, EmbeddingStoreContentRetriever filteredRetriever) {
        try {
            log.info("=================== 동적 AiService 생성 시작 ===================");
            log.info("사용자 프롬프트: {}", userPrompt);
            log.info("사용자 여가 시간: {} ~ {}", userStart, userEnd);



            ContentAiService dynamicAiService = AiServices.builder(ContentAiService.class)
                                                          .chatLanguageModel(chatModel)
                                                          .contentRetriever(filteredRetriever)  // 필터링된 retriever 주입
                                                          .build();
            log.info("동적 AiService 생성 완료");

            IdTitleReasonContentDTO mongoResult = dynamicAiService.quickRecommendContent(
                userPrompt);

            log.info("모임 추천 결과 ==================");
            for (IdTitleReasonDTO dto : mongoResult.event()) {
                log.info("id: {}", dto.id());
                log.info("title: {}", dto.title());
                log.info("추천이유 : {}", dto.reason());
            }

            RecommendContentDTO result = new RecommendContentDTO(new ArrayList<>());
            for (IdTitleReasonDTO group : mongoResult.event()) {
                Optional<GroupEmbedding> document = contentEmbeddingRepository.findByIdAndTitle(
                    group.id(), group.title());
                log.info("LLM의 추천답변(실존하는지 체크) id: {}, title: {}", group.id(), group.title());
                if (document.isPresent()) {
                    result.event()
                          .add(new RecommendDTO(Long.parseLong(document.get().getId()), group.reason()));
                    log.info("실제 아이디: {}", document.get().getId());
                }
            }

            log.info("추천 완료: {}개 결과", result.event() != null ? result.event().size() : 0);

            return result;

        } catch (Exception e) {
            log.error("동적 AiService 추천 중 오류 발생", e);
            return new RecommendContentDTO(null);
        }
    }

    public RecommendGroupDTO quickRecommendGroup(String prompt, Long userStart, Long userEnd,
        EmbeddingStoreContentRetriever filteredRetriever) {
        try {
            log.info("=================== 동적 AiService 생성 시작 ===================");
            log.info("사용자 프롬프트: {}", prompt);

            GroupAiService dynamicAiService = AiServices.builder(GroupAiService.class)
                                                        .chatLanguageModel(chatModel)
                                                        .contentRetriever(filteredRetriever)  // 필터링된 retriever 주입
                                                        .build();
            log.info("동적 AiService 생성 완료");

            IdTitleReasonGroupDTO mongoResult = dynamicAiService.quickRecommendGroup(prompt);

            log.info("모임 추천 결과 ==================");
            for (IdTitleReasonDTO dto : mongoResult.group()) {
                log.info("id: {}", dto.id());
                log.info("title: {}", dto.title());
                log.info("추천이유 : {}", dto.reason());
            }

            RecommendGroupDTO result = new RecommendGroupDTO(new ArrayList<>());
            for (IdTitleReasonDTO group : mongoResult.group()) {
                Optional<GroupEmbedding> document = groupEmbeddingRepository.findByIdAndTitle(
                    group.id(), group.title());
                log.info("LLM의 추천답변(실존하는지 체크) id: {}, title: {}", group.id(), group.title());
                if (document.isPresent()) {
                    result.group()
                          .add(new RecommendDTO(Long.parseLong(document.get().getId()), group.reason()));
                    log.info("실제 아이디: {}", document.get().getId());
                }
            }

            log.info("추천 완료: {}개 결과", result.group() != null ? result.group().size() : 0);

            return result;

        } catch (Exception e) {
            log.error("동적 AiService 추천 중 오류 발생", e);
            return new RecommendGroupDTO(null);
        }
    }

    public RecommendContentDTO quickRecommendPlace(String prompt, String userAddress,
        EmbeddingStoreContentRetriever filteredRetriever) {
        try {
            log.info("=================== 동적 AiService 생성 시작 ===================");
            log.info("사용자 프롬프트: {}", prompt);



            ContentAiService dynamicAiService = AiServices.builder(ContentAiService.class)
                                                          .chatLanguageModel(chatModel)
                                                          .contentRetriever(filteredRetriever)
                                                          .build();
            log.info("동적 AiService 생성 완료");

            IdTitleReasonContentDTO mongoResult = dynamicAiService.quickRecommendPlace(prompt);

            log.info("모임 추천 결과 ==================");
            for (IdTitleReasonDTO dto : mongoResult.event()) {
                log.info("id: {}", dto.id());
                log.info("title: {}", dto.title());
                log.info("추천이유 : {}", dto.reason());
            }

            RecommendContentDTO result = new RecommendContentDTO(new ArrayList<>());
            for (IdTitleReasonDTO group : mongoResult.event()) {
                Optional<GroupEmbedding> document = contentEmbeddingRepository.findByIdAndTitle(
                    group.id(), group.title());
                log.info("LLM의 추천답변(실존하는지 체크) id: {}, title: {}", group.id(), group.title());
                if (document.isPresent()) {
                    result.event()
                          .add(new RecommendDTO(Long.parseLong(document.get().getId()), group.reason()));
                    log.info("실제 아이디: {}", document.get().getId());
                }
            }

            log.info("추천 완료: {}개 결과", result.event() != null ? result.event().size() : 0);

            return result;

        } catch (Exception e) {
            log.error("동적 AiService 추천 중 오류 발생", e);
            return new RecommendContentDTO(null);
        }
    }


}

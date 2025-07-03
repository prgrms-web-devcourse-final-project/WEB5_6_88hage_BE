package com.grepp.funfun.app.model.recommend.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    chatModel = "googleAiGeminiChatModel"
)
public interface ContentsAiService {

    @SystemMessage("너는 여가시간에 하면 좋을 만한 활동을 추천해주는 코디네이터야")
    String chat(@UserMessage String message);

}

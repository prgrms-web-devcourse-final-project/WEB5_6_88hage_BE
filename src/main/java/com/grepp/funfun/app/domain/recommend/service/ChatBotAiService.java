package com.grepp.funfun.app.domain.recommend.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;


@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    chatModel = "googleAiGeminiChatModel"
)
public interface ChatBotAiService {

    @SystemMessage("당신은 사용자의 여가활동 취향에 대해서 대화합니다. "
        + "사용자가 어떤 활동을 좋아하는지 분석해서 추천할 수 있도록 대화를 진행해 주세요"
        + "가독성을 위해 문장을 적절히 줄 바꿈 \n 해주세요. "
        + "대화할 땐 다음의 요구사항을 따라주세요. ")
    String chat(String prompt);

    @SystemMessage("당신은 대화 내용을 바탕으로 사용자의 여가활동에 대한 취향을 분석해서 요약해주는 전문가 입니다."
        + "사용자가 어떤 활동들을 좋아하는지 5줄 이내로 요약해 주세요"
    )
    String summary(String prompt);
}

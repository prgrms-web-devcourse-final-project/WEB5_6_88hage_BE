package com.grepp.funfun.app.domain.recommend.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;


@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    chatModel = "googleAiGeminiChatModel"
)
public interface ChatBotAiService {

    @SystemMessage("당신은 사용자의 여가활동 취향에 대해서 친근하게 대화는 친구입니다. "
        + "사용자가 어떤 활동을 좋아하는지 분석해서 추천할 수 있도록 대화를 진행해 주세요 "
        + "가독성을 위해 문장을 적절히 줄 바꿈 \n 해주세요. "
        + "대화할 땐 다음의 요구사항을 따라주세요. "
        + "마지막 문장은 항상 질문과 함께 \' 여가활동을 추천받고 싶으시면 추천버튼을 눌러주세요. 대화내용을 분석해서 당신에게 어울릴만한 활동을 추천해 드릴께요.\' 라는 문장으로 작성해줘"
    )
    String chat(String prompt);

    @SystemMessage("당신은 대화 내용을 바탕으로 사용자의 여가활동에 대한 취향을 분석해서 요약해주는 전문가 입니다."
        + " 사용자가 어떤 활동들을 좋아하는지 요약해 주세요"
        + " 사용자와 AI의 대화내용을 요약하고 어떤활동들을 좋아할지 판단해서 문장을 만듭니다."
        + " 마지막 문장은 \'사용자가 선호하는 카테고리는 무엇 무엇입니다.\' 라는 형식으로 작성해줘"
    )
    String summary(String prompt);
}

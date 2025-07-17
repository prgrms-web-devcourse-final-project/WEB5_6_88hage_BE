package com.grepp.funfun.app.domain.recommend.dto.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "챗봇 대화 요청 정보, 가장 처음에는 chatBotHistory를 null로 설정",
        example = """
        {
          "chatBotHistory": [
            {
              "user": "안녕하세요, 제 일기 요약해 주실 수 있나요?",
              "ai": "네, 물론입니다. 일기 내용을 입력해 주시면 요약해 드릴게요."
            },
            {
              "user": "오늘 아침 일찍 일어나서 운동을 했어. 근데 너무 힘들어서 점심을 많이 먹어버렸지 뭐야.",
              "ai": "운동은 힘든 만큼 보람 있는 일이죠. 점심을 많이 드셨다고 너무 걱정하지 마세요. 오늘 하루를 잘 마무리하는 것이 더 중요해요!"
            }
          ],
          "userMessage": "그렇죠? 조언 고마워!"
        }
        """)
public class ChatBotRequest {

    @Schema(description = "과거 대화 이력 (선택 사항) 프론트 측에서 리스트 만들어서 관리해주셔야 합니다.")
    private List<ChatBotMessage> chatBotHistory;

    @Schema(description = "사용자의 현재 메시지", required = true)
    private String userMessage;

}

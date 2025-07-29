package com.grepp.funfun.app.domain.recommend.service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class ChatBotServiceTest {

    @Autowired
    private ChatBotService chatBotService;

    @Test
    public void test() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 7, 29, 18, 0, 0);

        // 요일 배열 (일요일=1, 월요일=2, ..., 토요일=7)
        String[] dayOfWeek = {"", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};

        // get 메서드들을 이용해서 각 값 추출
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int dayOfWeekValue = dateTime.getDayOfWeek()
                                     .getValue(); // 월요일=1, 화요일=2, ..., 일요일=7

        // 요일 인덱스 조정 (ISO 8601 기준: 월요일=1 -> 배열 인덱스: 월요일=2)
        String dayName = dayOfWeek[dayOfWeekValue];

        // 문자열 조합
        String result = String.format("나는 %d-%02d-%02d %s %d시 %02d분 부터 여가 시간이야",
                                      year, month, day, dayName, hour, minute);

        log.info(result);
    }

}
package com.grepp.funfun.app.domain.recommend.service;

import com.grepp.funfun.app.domain.recommend.dto.ChatBotDTO;
import com.grepp.funfun.app.domain.recommend.entity.ChatBot;
import com.grepp.funfun.app.domain.recommend.repository.ChatBotRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBotService {

    private final ChatBotRepository chatBotRepository;

    public static String getDate(LocalDateTime startTime, LocalDateTime endTime) {

        String[] dayOfWeek = {"", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};

        int year = startTime.getYear();
        int month = startTime.getMonthValue();
        int day = startTime.getDayOfMonth();
        int hour = startTime.getHour();
        int minute = startTime.getMinute();
        int dayOfWeekValue = startTime.getDayOfWeek().getValue(); // 월요일=1, 화요일=2, ..., 일요일=7

        int year2 = endTime.getYear();
        int month2 = endTime.getMonthValue();
        int day2 = endTime.getDayOfMonth();
        int hour2 = endTime.getHour();
        int minute2 = endTime.getMinute();
        int dayOfWeekValue2 = endTime.getDayOfWeek().getValue();

        String dayName = dayOfWeek[dayOfWeekValue];
        String dayName2 = dayOfWeek[dayOfWeekValue2];

        log.info("시작:, {} {} 끝: {} {}",dayOfWeekValue, dayName, dayOfWeekValue2, dayName2 );

        // 문자열 조합
        String start = String.format("나는 %d-%02d-%02d %s %d시 %02d분 부터 ",
                                      year, month, day, dayName, hour, minute);
        String end = String.format("%d-%02d-%02d %s %d시 %02d분 까지 여기시간이야 ",
                                      year2, month2, day2, dayName2, hour2, minute2);
        return start + end;
    }

    public Optional<ChatBot> findChatBotSummary(String email) {
        Optional<ChatBot> chatBot = chatBotRepository.findByEmailAndActivatedIsTrue(email);
        return chatBot;
    }

    @Transactional
    public void updateSummary(Long id, String summary) {
        chatBotRepository.updateSummary(id, summary);
    }

    @Transactional
    public void registSummary(ChatBot chatBot) {
        chatBotRepository.save(chatBot);
    }

    public List<ChatBotDTO> findAll() {
        final List<ChatBot> chatBots = chatBotRepository.findAll(Sort.by("id"));
        return chatBots.stream()
                .map(chatBot -> mapToDTO(chatBot, new ChatBotDTO()))
                .toList();
    }

    public ChatBotDTO get(final Long id) {
        return chatBotRepository.findById(id)
                .map(chatBot -> mapToDTO(chatBot, new ChatBotDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public void delete(final Long id) {
        chatBotRepository.deleteById(id);
    }

    private ChatBotDTO mapToDTO(final ChatBot chatBot, final ChatBotDTO chatBotDTO) {
        chatBotDTO.setId(chatBot.getId());
        chatBotDTO.setEmail(chatBot.getEmail());
        chatBotDTO.setGroupSummary(chatBot.getGroupSummary());
        chatBotDTO.setContentSummary(chatBot.getContentSummary());
        chatBotDTO.setType(chatBot.getType());
        return chatBotDTO;
    }



}

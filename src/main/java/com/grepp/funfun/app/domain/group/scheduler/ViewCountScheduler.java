package com.grepp.funfun.app.domain.group.scheduler;

import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final GroupRepository groupRepository;

    // 1분
    @Scheduled(fixedDelay = 60000)
    public void viewCountToDatabase() {
        try {
            Set<String> keys = redisTemplate.keys("group:*:viewCount");

            for (String key : keys) {
                Long groupId = extractGroupId(key);
                String viewCountStr = redisTemplate.opsForValue().get(key);

                if (viewCountStr != null) {
                    Integer viewCount = Integer.parseInt(viewCountStr);

                    groupRepository.findById(groupId).ifPresent(group -> {
                        group.updateViewCount(viewCount);
                        groupRepository.save(group);
                    });

                }
            }
        } catch (Exception e) {
            log.error("Failed to sync view count to database", e);
        }
    }

    private Long extractGroupId(String key) {
        String[] parts = key.split(":");
        return Long.parseLong(parts[1]);
    }
}

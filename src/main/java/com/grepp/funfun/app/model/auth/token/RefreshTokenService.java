package com.grepp.funfun.app.model.auth.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.funfun.app.model.auth.token.entity.RefreshToken;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RefreshToken saveWithAtId(String atId, long refreshTokenExpiration){
        RefreshToken refreshToken = new RefreshToken(atId);
        refreshToken.setTtl(refreshTokenExpiration);
        redisTemplate.opsForValue().set(atId, refreshToken, Duration.ofSeconds(refreshTokenExpiration));
        return refreshToken;
    }
    
    public void deleteByAccessTokenId(String atId) {
        redisTemplate.delete(atId);
    }
    
    public RefreshToken renewingToken(String id, String newTokenId){
        RefreshToken refreshToken = findByAccessTokenId(id);
        
        if(refreshToken == null) return null;

        // 기존 TTL 그대로 유지
        long remainingTtl = redisTemplate.getExpire(id, TimeUnit.SECONDS);
        if (remainingTtl <= 0) return null;
        
        // 지연시간 동안 사용할 ttl 10초 짜리
        RefreshToken gracePeriodToken = new RefreshToken(id);
        gracePeriodToken.setToken(refreshToken.getToken());
        
        // 기존 refresh token 변경
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setAtId(newTokenId);
        refreshToken.setTtl(remainingTtl);

        redisTemplate.opsForValue().set(newTokenId, refreshToken, Duration.ofSeconds(remainingTtl));
        redisTemplate.opsForValue().set(id, gracePeriodToken, Duration.ofSeconds(10));
        return refreshToken;
    }

    public RefreshToken findByAccessTokenId(String atId) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(atId), RefreshToken.class);
    }

}

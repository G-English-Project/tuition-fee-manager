//package com.hyudequeue.genglish.tuition_fee_manager.utility.jwt;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//
//@Service
//@RequiredArgsConstructor
//public class TokenBlacklistService {
//  private final StringRedisTemplate redisTemplate;
//  private static final String BLACKLIST_PREFIX = "blacklist:";
//
//  public void blacklistToken(String token, long expirationTime) {
//    redisTemplate
//        .opsForValue()
//        .set(BLACKLIST_PREFIX + token, "blacklisted", Duration.ofMillis(expirationTime));
//  }
//
//  public boolean isTokenBlacklisted(String token) {
//    return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
//  }
//}

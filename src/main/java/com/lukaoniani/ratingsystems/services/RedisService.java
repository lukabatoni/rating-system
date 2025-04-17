package com.lukaoniani.ratingsystems.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final StringRedisTemplate redisTemplate;

  public void saveConfirmationCode(String email, String code) {
    redisTemplate.opsForValue().set(email, code, 24, TimeUnit.HOURS);
  }

  public String getConfirmationCode(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  public void deleteConfirmationCode(String email) {
    redisTemplate.delete(email);
  }

  public String getEmailByResetCode(String code) {
    Set<String> keys = redisTemplate.keys("*");
    for (String key : keys) {
      String storedCode = redisTemplate.opsForValue().get(key);
      if (code.equals(storedCode)) {
        return key;
      }
    }
    return null;
  }


}

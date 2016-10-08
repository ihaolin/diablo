package me.hao0.diablo.server.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Simple Redis Id Generator
 * Author: haolin
 * Date:   8/25/16
 * Email:  haolin.h0@gmail.com
 */
@Component
public class RedisIds {

    @Autowired
    private StringRedisTemplate redis;

    /**
     * Generate id of the class
     * @param clazz the target class
     * @return the id of class
     */
    public Long generate(Class<?> clazz){
        String idGeneratorKey = RedisKeys.keyOfIdGenerator(clazz);
        return redis.opsForValue().increment(idGeneratorKey, 1);
    }
}

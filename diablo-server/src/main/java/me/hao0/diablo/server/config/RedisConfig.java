package me.hao0.diablo.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}

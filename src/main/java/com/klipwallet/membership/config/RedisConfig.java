package com.klipwallet.membership.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * {@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 */
@Profile("!local")
@Configuration(proxyBeanMethods = false)
public class RedisConfig {
    @Bean
    GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer(ObjectMapper objectMapper) {
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    /**
     * @see org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration#redisTemplate(org.springframework.data.redis.connection.RedisConnectionFactory)
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
                                                       GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setDefaultSerializer(jackson2JsonRedisSerializer);
        return template;
    }
}

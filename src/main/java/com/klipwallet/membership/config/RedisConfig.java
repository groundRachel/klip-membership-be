package com.klipwallet.membership.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.klipwallet.membership.entity.OperatorInvitation;

/**
 * {@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 */
@Profile("!local")
@Configuration(proxyBeanMethods = false)
public class RedisConfig {
    public static final String BEAN_OPERATOR_INVITATION_REDIS_TEMPLATE = "operatorInvitationRedisTemplate";


    @Bean(BEAN_OPERATOR_INVITATION_REDIS_TEMPLATE)
    RedisTemplate<String, OperatorInvitation> operatorInvitationRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, OperatorInvitation> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(OperatorInvitation.class));
        return template;
    }
}

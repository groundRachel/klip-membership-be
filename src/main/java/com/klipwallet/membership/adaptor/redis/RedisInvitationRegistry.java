package com.klipwallet.membership.adaptor.redis;

import java.util.UUID;

import jakarta.annotation.Nullable;

import lombok.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.config.DeployEnv;
import com.klipwallet.membership.config.KlipMembershipProperties;
import com.klipwallet.membership.entity.OperatorInvitation;
import com.klipwallet.membership.service.InvitationRegistry;

@Profile("!local")
@Component
public class RedisInvitationRegistry implements InvitationRegistry {
    private final RedisOperations<String, OperatorInvitation> redisTemplate;
    private final DeployEnv env;

    public RedisInvitationRegistry(RedisOperations<String, OperatorInvitation> redisOperations,
                                   KlipMembershipProperties properties) {
        this.redisTemplate = redisOperations;
        this.env = properties.getEnv();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String save(OperatorInvitation invitation) {
        String invitationCode = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.boundValueOps(toRedisKey(invitationCode)).set(invitation, DEFAULT_TIMEOUT);
        return invitationCode;
    }

    private String toRedisKey(String invitationCode) {
        return "%s:invitation-codes:%s".formatted(env.toDisplay(), invitationCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public OperatorInvitation lookup(String invitationCode) {
        return redisTemplate.boundValueOps(toRedisKey(invitationCode)).get();
    }

    @Override
    public void delete(String invitationCode) {
        redisTemplate.delete(toRedisKey(invitationCode));
    }
}

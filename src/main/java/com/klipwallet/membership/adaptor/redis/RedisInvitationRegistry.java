package com.klipwallet.membership.adaptor.redis;

import java.util.UUID;

import jakarta.annotation.Nullable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.OperatorInvitation;
import com.klipwallet.membership.service.InvitationRegistry;

@Profile("!local")
@Component
@RequiredArgsConstructor
public class RedisInvitationRegistry implements InvitationRegistry {
    private final RedisOperations<String, OperatorInvitation> redisOperations;

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public String save(OperatorInvitation invitation) {
        String code = UUID.randomUUID().toString().replace("-", "");
        redisOperations.boundValueOps(code).set(invitation, DEFAULT_TIMEOUT);
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public OperatorInvitation lookup(String invitationCode) {
        return redisOperations.boundValueOps(invitationCode).get();
    }
}

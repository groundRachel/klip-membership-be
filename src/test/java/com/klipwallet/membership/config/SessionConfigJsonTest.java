package com.klipwallet.membership.config;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;

import com.klipwallet.membership.config.security.KlipMembershipOAuth2User;
import com.klipwallet.membership.entity.MemberId;

import static com.klipwallet.membership.config.SecurityConfig.ROLE_ADMIN;
import static com.klipwallet.membership.config.SecurityConfig.ROLE_SUPER_ADMIN;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(SessionConfig.class)
class SessionConfigJsonTest {
    @Autowired
    RedisSerializer<Object> springSessionDefaultRedisSerializer;

    @Test
    void serializeAndDeserializeSession() {
        // given
        KlipMembershipOAuth2User oAuth2User = createOAuth2User();
        // when
        byte[] serialize = springSessionDefaultRedisSerializer.serialize(oAuth2User);
        KlipMembershipOAuth2User deserialize = (KlipMembershipOAuth2User) springSessionDefaultRedisSerializer.deserialize(serialize);
        // then
        assertThat(deserialize).isEqualTo(oAuth2User);
    }

    private KlipMembershipOAuth2User createOAuth2User() {
        List<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(ROLE_ADMIN, ROLE_SUPER_ADMIN);
        OAuth2AccessToken token = new OAuth2AccessToken(TokenType.BEARER, "dfadsfasdadfssdf", Instant.now(), Instant.now().plus(1, HOURS));
        return new KlipMembershipOAuth2User(
                new MemberId(1), Collections.emptyMap(), authorities, "jordan.jung", "jordan.jung@groundx.xyz", "01026382580", token);
    }
}
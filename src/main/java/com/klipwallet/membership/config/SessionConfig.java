package com.klipwallet.membership.config;

import java.io.Serial;

import jakarta.annotation.Nonnull;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;

import com.klipwallet.membership.config.security.KlipMembershipOAuth2User;
import com.klipwallet.membership.config.security.KlipMembershipOAuth2UserMixin;

@Configuration(proxyBeanMethods = false)
public class SessionConfig implements BeanClassLoaderAware {
    private ClassLoader loader;

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    /**
     * Customized {@link ObjectMapper} to add mix-in for class that doesn't have default
     * constructors
     *
     * @return the {@link ObjectMapper} to use
     */
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        mapper.registerModule(new KlipMembershipSecurityModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /*
     * @see
     * org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang
     * .ClassLoader)
     */
    @Override
    public void setBeanClassLoader(@Nonnull ClassLoader classLoader) {
        this.loader = classLoader;
    }

    public static class KlipMembershipSecurityModule extends SimpleModule {
        @Serial
        private static final long serialVersionUID = -445942348341277798L;

        public KlipMembershipSecurityModule() {
            super(KlipMembershipSecurityModule.class.getName(),
                  new Version(1, 0, 0, null, null, null));
        }

        @Override
        public void setupModule(SetupContext context) {
            SecurityJackson2Modules.enableDefaultTyping(context.getOwner());
            context.setMixInAnnotations(KlipMembershipOAuth2User.class, KlipMembershipOAuth2UserMixin.class);
        }

    }
}

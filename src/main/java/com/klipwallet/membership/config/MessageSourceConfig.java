package com.klipwallet.membership.config;

import java.time.Duration;
import java.util.Locale;

import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;

/**
 * 기본 제공 {@link org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration}에 의존하는 것이 아니라
 * 이것을 확장해서 {@link ReloadableResourceBundleMessageSource}를 이용해서 {@code messageSource}를 구현함.
 * <p>
 * 이를 통해서 <b>xml 프로퍼티</b> 스펙으로 메시지 UTF-8로 준비할 수 있음.
 * 기존 {@code properties} 스펙으로는 {@code ISO 8859-1} 만 대응 가능한데, 유니코드 문자열을 표현할 시 유니코드 리터럴로 표현해야함.
 * 물론 IDE에서 플러그인을 통해서 변환을 해주는 것이 있지만 github 등에서 직접 확인이 안되므로 xml으로 표현하는 것을 선호함.
 * </p>
 *
 * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 * @see MessageSourceProperties
 * @see ReloadableResourceBundleMessageSource
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
public class MessageSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Primary
    @Bean
    MessageSource messageSource(MessageSourceProperties properties) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        if (StringUtils.hasText(properties.getBasename())) {
            messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename())));
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setDefaultLocale(Locale.KOREAN);  // !!
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }
}

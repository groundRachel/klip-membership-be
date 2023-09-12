package com.klipwallet.membership.config;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import com.klipwallet.membership.exception.BaseException;
import com.klipwallet.membership.exception.ErrorCode;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

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
    public static final Locale DEFAULT_LOCALE = Locale.KOREAN;

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
        messageSource.setDefaultLocale(DEFAULT_LOCALE);  // !!
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }

    /**
     * 오류 코드 중복, 오류 메세지 존재 여부 확인
     */
    public static class ErrorCodeVerifier {
        ErrorCodeVerifier(MessageSource messageSource) {
            ErrorCode[] errorCodes = ErrorCode.values();
            verifyDuplicatedErrorCode(errorCodes);
            verifyCodeContainsMessageSource(messageSource, errorCodes);
        }

        public static void verify(MessageSource messageSource) {
            new ErrorCodeVerifier(messageSource);
        }

        private void verifyDuplicatedErrorCode(ErrorCode[] errorCodes) {
            Map<Integer, Long> codeCountMap = Stream.of(errorCodes)
                                                    .map(ErrorCode::getCode)
                                                    .collect(groupingBy(c -> c, counting()));
            for (Entry<Integer, Long> each : codeCountMap.entrySet()) {
                if (each.getValue() > 1L) {
                    throw new BaseException("Duplicated errorCode: %s".formatted(each.getKey()));
                }
            }
        }

        private void verifyCodeContainsMessageSource(MessageSource messageSource, ErrorCode[] errorCodes) {
            for (ErrorCode errorCode : errorCodes) {
                String code = errorCode.toMessageCode();
                String message = messageSource.getMessage(code, null, DEFAULT_LOCALE);
                if (notExistCode(code, message)) {
                    throw new BaseException("Not exists code: %s in messageSource".formatted(code));
                }
            }
        }

        private boolean notExistCode(String code, String message) {
            return code.equals(message);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @RequiredArgsConstructor
    @Slf4j
    public static class MessageVerifyConfig {
        private final MessageSource messageSource;

        @PostConstruct
        public void verifyErrorCodes() {
            ErrorCodeVerifier.verify(messageSource);
            log.info("verifyErrorCode");
        }
    }
}

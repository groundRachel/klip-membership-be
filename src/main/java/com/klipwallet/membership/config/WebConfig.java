package com.klipwallet.membership.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.klipwallet.membership.entity.AppliedPartner.Status;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
    }

    public class StringToEnumConverter implements Converter<String, Status> {
        @Override
        public Status convert(String source) {
            try {
                return Status.fromDisplay(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
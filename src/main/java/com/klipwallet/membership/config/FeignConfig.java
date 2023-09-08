package com.klipwallet.membership.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import com.klipwallet.membership.adaptor.AdaptorBase;

@Configuration
@EnableFeignClients(basePackageClasses = AdaptorBase.class)
@EnableConfigurationProperties({KakaoApiProperties.class, KasApiProperties.class, BgmsProperties.class, KlipDropsInternalProperties.class})
public class FeignConfig {

}


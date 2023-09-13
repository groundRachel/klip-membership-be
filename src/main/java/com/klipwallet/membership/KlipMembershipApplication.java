package com.klipwallet.membership;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import com.klipwallet.membership.config.KlipMembershipProperties;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
@EnableConfigurationProperties(KlipMembershipProperties.class)
public class KlipMembershipApplication {
    public static void main(String[] args) {
        SpringApplication.run(KlipMembershipApplication.class, args);
    }
}

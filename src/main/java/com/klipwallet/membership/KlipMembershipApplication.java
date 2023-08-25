package com.klipwallet.membership;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import com.klipwallet.membership.config.MessageSourceConfig.ErrorCodeVerifier;
import com.klipwallet.membership.repository.BaseRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = BaseRepository.class)
@EnableJpaAuditing
@EnableAsync
@RequiredArgsConstructor
public class KlipMembershipApplication {
    private final MessageSource messageSource;

    public static void main(String[] args) {
        SpringApplication.run(KlipMembershipApplication.class, args);
    }

    @PostConstruct
    public void verifyErrorCode() {
        ErrorCodeVerifier.verify(messageSource);
    }
}

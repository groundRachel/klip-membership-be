package com.klipwallet.membership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.klipwallet.membership.config.security.MemberIdAuditorProvider;
import com.klipwallet.membership.repository.BaseRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = BaseRepository.class)
@EnableJpaAuditing
@EnableFeignClients
public class KlipMembershipApplication {
    public static void main(String[] args) {
        SpringApplication.run(KlipMembershipApplication.class, args);
    }

    /**
     * {@link org.springframework.data.annotation.CreatedBy}, {@link org.springframework.data.annotation.LastModifiedBy} 를 위한 Bean
     * {@link KlipMembershipApplication} 에 위치해야함.
     */
    @Bean
    MemberIdAuditorProvider memberIdAuditorProvider() {
        return new MemberIdAuditorProvider();
    }
}

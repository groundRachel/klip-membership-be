package com.klipwallet.membership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.klipwallet.membership.repository.BaseRepository;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = BaseRepository.class)
public class KmtApplication {
    public static void main(String[] args) {
        SpringApplication.run(KmtApplication.class, args);
    }
}

package com.klipwallet.membership.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.klipwallet.membership.repository.BaseRepository;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackageClasses = BaseRepository.class)
@EnableJpaAuditing
public class JpaConfig {

}

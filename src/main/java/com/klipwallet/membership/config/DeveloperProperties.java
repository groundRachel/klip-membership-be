package com.klipwallet.membership.config;


import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * local 환경 개발자 구성
 */
@ConfigurationProperties(prefix = "application.developer")
@Value
@Validated
public class DeveloperProperties {
    /**
     * 슈퍼 어드민 이메일
     * <pre>gene.goh@groundx.xyz</pre>
     */
    @NotNull @Email
    String superAdmin;
    /**
     * 개발자 이메일 목록. 관리자로 바로 등록시킴
     * <p>
     * 최초 개발자들: jordan, ian, winnie, rachel, ted, twinsen, sello
     * </p>
     */
    @NotEmpty
    Set<String> emails;
}

package com.klipwallet.membership.config.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import static com.klipwallet.membership.config.SecurityConfig.ROLE_KLIP_KAKAO;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithAuthenticatedUser(authorities = ROLE_KLIP_KAKAO)
public @interface WithKakaoUser {
    @AliasFor(annotation = WithAuthenticatedUser.class)
    int memberId() default 0;

    @AliasFor(annotation = WithAuthenticatedUser.class)
    String name() default "2959264750";

    @AliasFor(annotation = WithAuthenticatedUser.class)
    String email() default "jordan.gx@kakaocorp.com";

    @AliasFor(annotation = WithAuthenticatedUser.class)
    String kakaoPhoneNumber() default "+82 10-2638-2580";
}

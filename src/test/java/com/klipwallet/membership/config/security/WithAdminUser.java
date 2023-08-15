package com.klipwallet.membership.config.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.klipwallet.membership.config.SecurityConfig.ROLE_ADMIN;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithAuthenticatedUser(authorities = ROLE_ADMIN)
public @interface WithAdminUser {

    @AliasFor(annotation = WithAuthenticatedUser.class)
    int memberId() default 23;

    @AliasFor(annotation = WithAuthenticatedUser.class)
    String name() default "115419318504487812016";

    @AliasFor(annotation = WithAuthenticatedUser.class)
    String email() default "jordan.jung@groundx.xyz";
}

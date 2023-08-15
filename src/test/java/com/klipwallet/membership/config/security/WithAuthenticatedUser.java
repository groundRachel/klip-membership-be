package com.klipwallet.membership.config.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAuthenticatedUserSecurityContextFactory.class)
public @interface WithAuthenticatedUser {
    int memberId() default 23;

    String name() default "115419318504487812016";

    String email() default "jordan.jung@groundx.xyz";

    String[] authorities() default {"ROLE_PARTNER"};
}

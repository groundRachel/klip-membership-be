package com.klipwallet.membership.dto;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Pattern(regexp = "^[\\d-]+$")
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface PhoneNumber {

    String message() default "전화번호 형식과 맞지 않습니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

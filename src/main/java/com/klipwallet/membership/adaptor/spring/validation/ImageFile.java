package com.klipwallet.membership.adaptor.spring.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 파일 업로드 시 이미지만 업로드 가능한 Validation Annotation
 * <pre>
 *     {@code @PostMapping("/upload-image")}
 *     public AttachFileDto.Meta upload(@OnlyImage @RequestParam("file") MultipartFile file) {
 *         ...
 *     }
 * </pre>
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFileValidator.class})
public @interface ImageFile {
    String message() default "이미지 파일만 업로드 가능합니다.(jpeg, png, gif)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

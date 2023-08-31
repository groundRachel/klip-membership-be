package com.klipwallet.membership.adaptor.spring.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 파일 업로드 시 이미지만 업로드 가능한 Validation Annotation.
 * <p>
 * 현재 jpeg, png만 업로드 가능함.
 * </p>
 * 코드 사용 예시
 * <pre>
 *     {@code @PostMapping("/upload-image")}
 *     public AttachFileDto.Meta upload(@ImageFile @RequestParam("file") MultipartFile file) {
 *         ...
 *     }
 * </pre>
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFileValidator.class})
public @interface ImageFile {
    String message() default "이미지 파일만 업로드 가능합니다.(jpeg, png)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.klipwallet.membership.adaptor.jpa;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JPA 기본 설정 때문에 설정하는 생성자나 메서드에 사용하기 위한 애노테이션.
 * IDE의 정적 소스 코드 분석 경고를 우회하는 용도.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ForJpa {
}

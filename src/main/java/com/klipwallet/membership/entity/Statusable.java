package com.klipwallet.membership.entity;

import java.util.stream.Stream;

import jakarta.annotation.Nullable;

import org.apache.commons.text.CaseUtils;

/**
 * {@link java.lang.Enum}상태를 저장할 수 있는 인터페이스
 * <p>
 * DB 등으로 영속화 시 저장 공간을 줄이기 위한 인터페이스.
 * 일반적으로 {@code enum}으로 된 속성을 관리하기 위해서 사용한다.
 * 마지막으로 해당 인터페이스를 상속받은 {@link java.lang.Enum}의
 * {@code EnumConstant}명과 {@code code}값은 <b>절대로 변경하면 안된다.</b>
 * </p>
 */
public interface Statusable {
    private static void checkCodeRange(int code) {
        if (code < Byte.MIN_VALUE || code > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("Overflow code: " + code);
        }
    }

    @Nullable
    static <T extends Enum<T> & Statusable> T fromCode(Class<T> sClass, byte code) {
        return Stream.of(sClass.getEnumConstants())
                     .filter(e -> e.getCode() == code)
                     .findFirst()
                     .orElse(null);
    }

    @Nullable
    static <T extends Enum<T> & Statusable> T fromDisplay(Class<T> sClass, String display) {
        return Stream.of(sClass.getEnumConstants())
                     .filter(e -> matchedBy(e, display))
                     .findFirst()
                     .orElse(null);
    }

    private static <T extends Enum<T> & Statusable> boolean matchedBy(T ec, String display) {
        return ec.toDisplay().equals(display) || ec.name().equalsIgnoreCase(display);
    }

    static byte requireVerifiedCode(int code) {
        checkCodeRange(code);
        return (byte) code;
    }

    /**
     * 상태에 대한 단순화한 코드
     * <p>
     * db로 반환할 시 용량을 최소화 하기 위해서 {@code byte}로 변환
     * mysql 기준 {@code tinyint} 타입으로 저장
     * </p>
     */
    byte getCode();

    /**
     * 일반적으로 Enum Constant 명
     */
    String name();

    /**
     * 노출명
     */
    default String toDisplay() {
        return CaseUtils.toCamelCase(name(), false, '_');
    }
}

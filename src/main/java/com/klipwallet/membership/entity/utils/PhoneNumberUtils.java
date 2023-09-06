package com.klipwallet.membership.entity.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class PhoneNumberUtils {
    private PhoneNumberUtils() {
        throw new UnsupportedOperationException("This is utility class");
    }

    /**
     * 한국의 휴대펀 번호가 아니면 예외 발생
     * <p>
     * 세부 스펙은 {@link #isFormalKrMobileNumber(String)} 참고
     * </p>
     *
     * @param mobileNumber 검사할 한국 휴대폰 번호
     * @throws java.lang.IllegalArgumentException 휴대폰 번호가 유효하지 않는 경우 발생
     */
    public static void checkKrMobileNumber(String mobileNumber) {
        if (isFormalKrMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException("Invalid mobile number: %s".formatted(mobileNumber));
        }
    }

    /**
     * KlipMembership 형식에 맞는 한국 휴대폰 번호인가?
     * <p>
     * 1. 모두 숫자여야함.(공백 미허용)
     * 2. 01로 시작해야함.
     * 3. 길이가 10 or 11
     * </p>
     *
     * @param mobileNumber 검사할 한국 휴대폰 번호
     * @return true 이면 유효한 한국 휴대폰 번호, false 이면 유효하지 않은 한국 휴대폰 번호
     */
    public static boolean isFormalKrMobileNumber(String mobileNumber) {
        if (!StringUtils.isNumeric(mobileNumber)) {
            return false;
        }
        if (!mobileNumber.startsWith("01")) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (mobileNumber.length() < 10 || mobileNumber.length() > 11) {
            return false;
        }
        return true;
    }

    /**
     * KlipMembership 형식에 맞는 한국 휴대폰 번호로 변경
     *
     * @throws java.lang.IllegalArgumentException 한국 휴대폰 번호 형식으로 변경되지 않으면 예외 발생
     */
    public static String toFormalKrMobileNumber(String mobileNumber) {
        if (mobileNumber == null) {
            return null;
        }
        mobileNumber = mobileNumber.replace("+82", "")
                                   .replace("-", "");
        mobileNumber = StringUtils.deleteWhitespace(mobileNumber);
        if (mobileNumber.startsWith("1")) {
            return "0%s".formatted(mobileNumber);
        }
        if (!isFormalKrMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException("Failed to convert format kr mobile number: %s".formatted(mobileNumber));
        }
        return mobileNumber;
    }
}

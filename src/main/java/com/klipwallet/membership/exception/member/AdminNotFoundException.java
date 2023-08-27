package com.klipwallet.membership.exception.member;

import lombok.NonNull;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

/**
 * <p>
 * 어드민을 찾을 수 없습니다. ID: {0,number,#}
 * </p>
 */
@SuppressWarnings("serial")
public class AdminNotFoundException extends NotFoundException {
    public AdminNotFoundException(@NonNull Integer adminId) {
        super(ErrorCode.ADMIN_NOT_FOUND, adminId);
    }
}

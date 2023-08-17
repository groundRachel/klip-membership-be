package com.klipwallet.membership.exception.member;

import com.klipwallet.membership.exception.NotFoundException;

public class PartnerNotFoundException extends NotFoundException {
    public PartnerNotFoundException(Integer id) {
        super("ID %d에 대한 파트너 정보를 조회할 수 없습니다.".formatted(id));
    }
}

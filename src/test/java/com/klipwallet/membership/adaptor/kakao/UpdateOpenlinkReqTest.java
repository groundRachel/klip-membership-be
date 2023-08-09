package com.klipwallet.membership.adaptor.kakao;

import org.junit.jupiter.api.Test;

import com.klipwallet.membership.adaptor.kakao.dto.UpdateOpenlinkReq;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateOpenlinkReqTest {
    @Test
    void constructorCheckNull() {
        assertThrows(NullPointerException.class, () -> {
            new UpdateOpenlinkReq(null, null, null, null, null, null);
        });
    }
}

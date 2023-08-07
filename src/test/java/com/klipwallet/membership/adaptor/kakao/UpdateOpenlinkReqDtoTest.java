package com.klipwallet.membership.adaptor.kakao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateOpenlinkReqDtoTest {
    @Test
    void constructorCheckNull() {
        assertThrows(NullPointerException.class, () -> {
            new UpdateOpenlinkReqDto(null, null, null, null, null, null, null);
        });
    }
}

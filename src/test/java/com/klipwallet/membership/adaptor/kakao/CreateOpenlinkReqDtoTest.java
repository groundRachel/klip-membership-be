package com.klipwallet.membership.adaptor.kakao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateOpenlinkReqDtoTest {

    @Test
    void constructorCheckNull() {
        assertThrows(NullPointerException.class, () -> {
            new CreateOpenlinkReqDto(null, null, null, null, null, null, null, null);
        });
    }

}

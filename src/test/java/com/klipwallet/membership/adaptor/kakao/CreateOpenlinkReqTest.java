package com.klipwallet.membership.adaptor.kakao;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import com.klipwallet.membership.adaptor.kakao.dto.CreateOpenlinkReq;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestPropertySource(locations = "classpath:kakao-api-test.properties")
class CreateOpenlinkReqTest {

    @Test
    void constructorCheckNull() {
        assertThrows(NullPointerException.class, () -> {
            new CreateOpenlinkReq(null, null, null, null, null, null, null);
        });
    }

}

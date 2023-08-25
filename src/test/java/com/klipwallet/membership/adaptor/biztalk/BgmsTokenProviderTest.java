package com.klipwallet.membership.adaptor.biztalk;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

@Disabled("최초 1회만 테스트 성공하면 됨.")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class BgmsTokenProviderTest {
    @Autowired
    BgmsTokenProvider bgmsTokenProvider;
    @SpyBean
    BgmsApiClient bgmsApiClient;

    @Test
    void prepareToken2Times() {
        BgmsToken bgmsToken = bgmsTokenProvider.prepareToken();

        assertThat(bgmsToken).isNotNull();
        assertThat(bgmsToken.getToken()).isNotBlank();
        assertThat(bgmsToken.getExpiredAt()).isAfter(LocalDateTime.now().plusHours(23));

        BgmsToken bgmsToken2 = bgmsTokenProvider.prepareToken();
        // 24시간 만료시간을 가지고 있어서 기존 것이 그대로 유지된다.
        assertThat(bgmsToken2).isEqualTo(bgmsToken);
        // 발급 받는 토큰은 서버에서 24시간 동안 유효하므로, 실제 api 호출은 한 번만 일어난다.
        then(bgmsApiClient).should(only()).getToken(any());
    }
}
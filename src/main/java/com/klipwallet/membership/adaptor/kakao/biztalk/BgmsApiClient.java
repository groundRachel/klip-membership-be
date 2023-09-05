package com.klipwallet.membership.adaptor.kakao.biztalk;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "bgms")
public interface BgmsApiClient {

    /**
     * 사용자 토큰 요청
     * <p>
     * BGMS 메시징 서비스를 이용하기 위한 인증토큰 발행을 요청 한다.<br/>
     * 인증토큰의 expire를 설정하지 않을 경우 유효기간이 24시간으로 설정된다.<br/>
     * 한 서버에서 발급받은 인증 토큰은 IP가 다른 서버에서 사용이 불가능하다.<br/>
     * 클라우드 환경 등 IP주소가 지정된 대역폭 이내에서 변경되는 경우 비즈톡으로 별도로 문의하도록 한다.
     * </p>
     */
    @PostMapping(value = "/v2/auth/getToken", consumes = MediaType.APPLICATION_JSON_VALUE)
    BgmsTokenRes getToken(@RequestBody BgmsTokenReq req);
}

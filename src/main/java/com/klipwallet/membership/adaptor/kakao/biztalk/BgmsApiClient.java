package com.klipwallet.membership.adaptor.kakao.biztalk;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsBaseRes;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsGetResultAllRes;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsSendAlimTalkReq;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsTokenReq;
import com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsTokenRes;

@FeignClient(name = "bgms")
public interface BgmsApiClient {
    String HEADER_BT_TOKEN = "bt-token";

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


    /**
     * 카카오 알림톡 전송을 요청한다.
     *
     * @param token BGMS 사용자 토큰
     * @param req   요청 RequestBody
     * @return 카카오 알림톡 발송 요청 응답 정보. 이 응답이 성공이라고, 카카오 알림 발송이 성공한 것은 아니다.(비동기)
     */
    @PostMapping(value = "/v2/kko/sendAlimTalk", consumes = MediaType.APPLICATION_JSON_VALUE)
    BgmsBaseRes sendAlimTalk(@RequestHeader(HEADER_BT_TOKEN) String token, @RequestBody BgmsSendAlimTalkReq req);

    /**
     * 전송 결과 일괄 요청
     * <p>
     * 해당 BSID({@link com.klipwallet.membership.config.BgmsProperties#getId()})로 전송 요청한 메시지 중 발송 처리된 메시지의 결과를 가져온다. (1회 최대 500건)
     * </p>
     *
     * @param token BGMS 사용자 토큰
     * @return 전송 결과 목록
     */
    @GetMapping(value = "/v2/kko/getResultAll", consumes = MediaType.APPLICATION_JSON_VALUE)
    BgmsGetResultAllRes getResultAll(@RequestHeader(HEADER_BT_TOKEN) String token);
}

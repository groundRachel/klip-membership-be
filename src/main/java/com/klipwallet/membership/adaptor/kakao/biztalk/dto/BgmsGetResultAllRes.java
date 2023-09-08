package com.klipwallet.membership.adaptor.kakao.biztalk.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/**
 * BGMS 전송 요철 결과들(MAX: 500)
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BgmsGetResultAllRes extends BgmsBaseRes {
    @JsonProperty("response")
    List<Response> responses;

    public BgmsGetResultAllRes(@JsonProperty("responseCode") String responseCode,
                               @JsonProperty("msg") String msg,
                               @JsonProperty("response") List<Response> responses) {
        super(responseCode, msg);
        this.responses = responses;
    }

    @SuppressWarnings("ClassCanBeRecord")
    @Value
    public static class Response {
        /**
         * 비즈톡에서 부여한 메시지 고유 ID
         */
        String uid;
        /**
         * 메시지 일련번호
         */
        String msgIdx;
        /**
         * 카카오 전송 결과
         */
        String resultCode;
        /**
         * 벤더사 -> 수신자 발송 요청 시간
         */
        String requestAt;
        /**
         * 벤더사 결과 수신 시간
         */
        String receivedAt;
        /**
         * 메시지 전송에 사용한 bsid
         */
        String bsid;

        public Response(@JsonProperty("uid") String uid,
                        @JsonProperty("msgIdx") String msgIdx,
                        @JsonProperty("resultCode") String resultCode,
                        @JsonProperty("requestAt") String requestAt,
                        @JsonProperty("receivedAt") String receivedAt,
                        @JsonProperty("bsid") String bsid) {
            this.uid = uid;
            this.msgIdx = msgIdx;
            this.resultCode = resultCode;
            this.requestAt = requestAt;
            this.receivedAt = receivedAt;
            this.bsid = bsid;
        }

        public boolean isSuccessful() {
            return CODE_SUCCESS.equals(receivedAt);
        }
    }
}

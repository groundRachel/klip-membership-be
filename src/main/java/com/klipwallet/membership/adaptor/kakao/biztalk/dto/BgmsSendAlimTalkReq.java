package com.klipwallet.membership.adaptor.kakao.biztalk.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import org.springframework.util.StringUtils;


/**
 * BGMS 카카오 알림톡 발송 요청 Request DTO
 */
public record BgmsSendAlimTalkReq(String msgIdx,
                                  String countryCode,
                                  String senderKey,
                                  String recipient,
                                  String appUserId,
                                  String orgCode,
                                  String message,
                                  String tmpltCode,
                                  String title,
                                  String resMethod,
                                  Integer price,
                                  String currencyType,
                                  Attach attach
) {
    /**
     * BGMS 카카오 알림톡 발송 요청 Request DTO
     * <p>
     * *가 뒤에 마킹된 인자는 필수입력
     * </p>
     *
     * @param msgIdx       메시지 일련번호* (메시지 고유값) 전송 시스템에서는 고유한지 확인하지 않으므로 사용자 측에서 고유한 값으로 관리되어야 한다.
     * @param countryCode  국가코드* 예) 82
     * @param senderKey    카카오* 발신 프로필 키
     * @param recipient    수신자 번호 예) 01012345678, 010-1234-5678 (recipient 또는 appUserId 중 1가지 필수 입력)
     * @param appUserId    앱유저 아이디 (recipient 또는 appUserId 중 1가지 필수 입력)
     * @param orgCode      고객사에서 지정한 부서 구분 코드
     * @param message      발신 메시지 내용* (공백 포함2345 1000자로 제한) 가변 영역이 있을 경우 해당 가변 영역의 내용도 실제 보 낼 내용으로 치환 되어야 한다. 예) 템플릿 : 안녕하세요 #{고객명}님. message:안녕하세요 비즈톡님.
     * @param tmpltCode    등록한 템플릿의 템플릿 코드*
     * @param title        템플릿 내용 중 강조 표기할 핵심 정보
     * @param resMethod    전송 방식* : PUSH
     * @param price        사용자에게 전달될 메시지 내 포함된 가격/금액/결제금액
     * @param currencyType 사용자에게 전달될 메시지 내 포함된 가격/금액/결제금액의 통화 단위(KRW, USD, EUR 등 국제 통화 코드)
     * @param attach       버튼 정보 JSON
     */
    public BgmsSendAlimTalkReq(@NonNull String msgIdx, @NonNull String countryCode, @NonNull String senderKey, String recipient, String appUserId,
                               String orgCode, @NonNull String message, @NonNull String tmpltCode, String title, @NonNull String resMethod,
                               Integer price, String currencyType, Attach attach) {
        verifyTarget(recipient, appUserId);
        this.msgIdx = msgIdx;
        this.countryCode = countryCode;
        this.senderKey = senderKey;
        this.recipient = recipient;
        this.appUserId = appUserId;
        this.orgCode = orgCode;
        this.message = message;
        this.tmpltCode = tmpltCode;
        this.title = title;
        this.resMethod = resMethod;
        this.price = price;
        this.currencyType = currencyType;
        this.attach = attach;
    }

    /**
     * BGMS 카카오 알림톡 발송 요청 Request DTO simple 정적 생성자
     * <p>
     * 대부분의 상황에서 본 정적 메서드로 커버할 수 있음.
     * </p>
     *
     * @param msgIdx    메시지 일련번호* (메시지 고유값) 전송 시스템에서는 고유한지 확인하지 않으므로 사용자 측에서 고유한 값으로 관리되어야 한다.
     * @param senderKey 카카오* 발신 프로필 키
     * @param recipient 수신자 번호 예) 01012345678, 010-1234-5678
     * @param message   발신 메시지 내용* (공백 포함2345 1000자로 제한) 가변 영역이 있을 경우 해당 가변 영역의 내용도 실제 보 낼 내용으로 치환 되어야 한다. 예) 템플릿 : 안녕하세요 #{고객명}님. message:안녕하세요 비즈톡님.
     * @param tmpltCode 등록한 템플릿의 템플릿 코드*
     * @param title     템플릿 내용 중 강조 표기할 핵심 정보
     * @param button    버튼 정보
     */
    public static BgmsSendAlimTalkReq recipient(String msgIdx, String senderKey, String recipient, String message,
                                                String tmpltCode, String title, Button button) {

        return new BgmsSendAlimTalkReq(msgIdx, "82", senderKey, recipient, null, null, message,
                                       tmpltCode, title, "PUSH", null, null, new Attach(List.of(button)));
    }

    private void verifyTarget(String recipient, String appUserId) {
        if (!StringUtils.hasText(recipient) && !StringUtils.hasText(appUserId)) {
            throw new IllegalArgumentException("One of 'recipient' and 'appUserId' must exist");
        }
    }

    public enum ButtonType {
        /**
         * url_mobile*: Mobile 환경에서 버튼 클릭 시 이동할 url
         * url_pc: PC환경에서 버튼 클릭 시 이동할 url
         */
        WL,
        /**
         * 버튼 클릭 시 배송조회 페이지로 이동
         */
        DS,
        /**
         * 해당 버튼 텍스트 전송
         */
        BK,
        /**
         * scheme_android: scheme_ios,scheme_android,url_mobile중 2가지 필수 입력. mobileandroid환경에서 버튼 클릭시 실행할 applicationcustom scheme
         * scheme_ios: mobile iOS 환경에서 버튼 클릭 시 실행 할 applicat ion custom scheme
         * url_mobile: Mobile 환경에서 버튼 클릭 시 이동할 url
         * url_pc: PC환경에서 버튼 클릭 시 이동할 url
         */
        AL,
        /**
         * 해당버튼텍스트+ 메시지본문 전송
         */
        MD,
        /**
         * chat_extra: 상담톡전환시전달할메타 정보
         */
        BC,
        /**
         * chat_extra: 봇전환시전달할메타 정보
         * char_event: 봇전환시연결할봇이벤트 명
         */
        BT,
        /**
         * 버튼 클릭 시 카카오톡 채널 추가
         */
        AC
    }

    /**
     * 버튼JSON
     *
     * @param buttons 버튼 목록
     */
    public record Attach(@JsonProperty("button") List<Button> buttons) {
    }

    /**
     * 버튼
     *
     * @param name          *버튼 제목
     * @param type          *버튼 타입 {@link com.klipwallet.membership.adaptor.kakao.biztalk.dto.BgmsSendAlimTalkReq.Button}
     * @param schemeAndroid Mobile android 환경에서 버튼 클릭 시 실 행할 application custom scheme
     * @param schemeIos     MobileiOS환경에서 버튼 클릭 시 실행 할 application custom scheme
     * @param urlMobile     Mobile 환경에서 버튼 클릭 시 이동할 url
     * @param urlPc         PC환경에서 버튼 클릭 시 이동할 url
     * @param chatExtra     상담톡/봇 전환 시 전달할 메타 정보
     * @param chatEvent     봇전환시연결할봇이벤트 명
     */
    public record Button(@NonNull String name,
                  @NonNull ButtonType type,
                  @JsonProperty("scheme_android") String schemeAndroid,
                  @JsonProperty("scheme_ios") String schemeIos,
                  @JsonProperty("url_mobile") String urlMobile,
                  @JsonProperty("url_pc") String urlPc,
                  @JsonProperty("chat_extra") String chatExtra,
                  @JsonProperty("chat_event") String chatEvent) {

        public static Button wl(String name, @NonNull String urlMobile, String urlPc) {
            return new Button(name, ButtonType.WL, null, null, urlMobile, urlPc, null, null);
        }

        public static Button al(String name, @NonNull String schemeAndroid, @NonNull String schemeIos, @NonNull String urlMobile, String urlPc) {
            return new Button(name, ButtonType.AL, schemeAndroid, schemeIos, urlMobile, urlPc, null, null);
        }
    }
}

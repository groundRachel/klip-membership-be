package com.klipwallet.membership.exception.operator;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.InvalidRequestException;

/**
 * 이미 등록된 운영진은 초대할 수 없습니다.
 * <p>
 * 초대 실패 안내: 이미 등록된 경우
 * </p>
 *
 * @see <a href="https://www.figma.com/file/SViFNQzT2wMNplAB7Xpznn/%5B%EC%9B%90%EB%B3%B8%5D-%ED%81%B4%EB%A6%BD-%ED%8C%A8%EC%8A%A4_v1.0.0?type=design&node-id=33562-32607&mode=design&t=pdw9nb1XjDyyxlZp-4">figma</a>
 */
@SuppressWarnings("serial")
public class OperatorInviteeAlreadyJoinedException extends InvalidRequestException {
    public OperatorInviteeAlreadyJoinedException() {
        super(ErrorCode.OPERATOR_INVITEE_ALREADY_JOINED);
    }
}

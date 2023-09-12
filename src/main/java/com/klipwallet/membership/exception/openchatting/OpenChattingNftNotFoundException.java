package com.klipwallet.membership.exception.openchatting;

import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.exception.NotFoundException;

public class OpenChattingNftNotFoundException extends NotFoundException {
    public OpenChattingNftNotFoundException() {
        super(ErrorCode.OPEN_CHATTING_NFT_NOT_FOUND);
    }
}

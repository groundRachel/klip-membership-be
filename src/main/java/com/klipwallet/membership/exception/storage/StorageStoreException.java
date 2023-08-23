package com.klipwallet.membership.exception.storage;

import java.io.Serial;

import com.klipwallet.membership.exception.BaseCodeException;
import com.klipwallet.membership.exception.ErrorCode;

public class StorageStoreException extends BaseCodeException {
    @Serial
    private static final long serialVersionUID = 2807481101638879250L;

    public StorageStoreException(Throwable cause) {
        super(ErrorCode.STORAGE_STORE, cause);
    }
}

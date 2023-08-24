package com.klipwallet.membership.dto.storage;

import java.io.Serial;
import java.io.Serializable;

import com.klipwallet.membership.entity.ObjectId;

public record StorageResult(ObjectId objectId, String objectUrl) implements Serializable {
    @Serial
    private static final long serialVersionUID = -4419724155394908795L;

}

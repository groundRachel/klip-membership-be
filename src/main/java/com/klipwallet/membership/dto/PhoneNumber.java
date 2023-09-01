package com.klipwallet.membership.dto;

import jakarta.validation.constraints.Pattern;

@Pattern(regexp = "^[\\d-]+$")
public @interface PhoneNumber {
}

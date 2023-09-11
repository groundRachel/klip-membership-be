package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.SignUpStatus;

public interface PartnerApplicationGettable {

    SignUpStatus getSignUpStatus(AuthenticatedUser user);
}

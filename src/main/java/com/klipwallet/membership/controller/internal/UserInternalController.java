package com.klipwallet.membership.controller.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.entity.AuthenticatedUser;

@RestController
@Slf4j
public class UserInternalController {
    @GetMapping("/internal/v1/user")
    public AuthenticatedUser user(@AuthenticationPrincipal AuthenticatedUser principal) {
        log.info("principal = {}\n{}", System.identityHashCode(principal), principal);
        return principal;
    }
}

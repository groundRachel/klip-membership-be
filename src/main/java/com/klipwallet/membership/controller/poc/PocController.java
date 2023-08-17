package com.klipwallet.membership.controller.poc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.config.security.KlipMembershipOAuth2User;
import com.klipwallet.membership.entity.AuthenticatedUser;

@RestController
@Slf4j
public class PocController {
    @GetMapping("/oauth")
    public OAuth2User oauth(@AuthenticationPrincipal OAuth2User principal) {
        return principal;
    }

    @GetMapping("/ouser")
    public OAuth2User ouser(@AuthenticationPrincipal OAuth2User principal) {
        log.info("principal = {}", System.identityHashCode(principal));
        return principal;
    }

    @GetMapping("/auser")
    public AuthenticatedUser auser(@AuthenticationPrincipal AuthenticatedUser principal) {
        log.info("principal = {}", System.identityHashCode(principal));
        return principal;
    }

    @GetMapping("/user")
    public KlipMembershipOAuth2User user(@AuthenticationPrincipal KlipMembershipOAuth2User principal) {
        log.info("principal = {}", System.identityHashCode(principal));
        return principal;
    }

    @GetMapping("/tool/user")
    public AuthenticatedUser toolUser(@AuthenticationPrincipal AuthenticatedUser principal) {
        return principal;
    }

    @GetMapping("/admin/user")
    public AuthenticatedUser adminUser(@AuthenticationPrincipal AuthenticatedUser principal) {
        return principal;
    }
}

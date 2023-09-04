package com.klipwallet.membership.config.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.klipwallet.membership.config.KlipMembershipProperties;

import static com.klipwallet.membership.config.SecurityConfig.ROLE_KLIP_KAKAO;

public class KlipMembershipOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final KlipMembershipProperties properties;

    public KlipMembershipOAuth2SuccessHandler(KlipMembershipProperties properties) {
        this.properties = properties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        if (isKakao()) {
            // 카카온 인증의 경우 > 운영진 초대
            response.sendRedirect("%s/landing/invite-operator/result?status=success".formatted(properties.getToolFrontUrl()));
            return;
        }
        if (isAdmin()) {
            response.sendRedirect(properties.getAdminFrontUrl());
            return;
        }
        // TODO @Jordan 파트너 신청의 경우 redirection 분기 추가 가능
        response.sendRedirect(properties.getToolFrontUrl());
    }

    private boolean isKakao() {
        Authentication authentication = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_KLIP_KAKAO));
    }

    private boolean isAdmin() {
        return KlipMembershipOAuth2UserService.isAdmin();
    }
}

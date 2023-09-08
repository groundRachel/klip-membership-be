package com.klipwallet.membership.config.security;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.klipwallet.membership.config.KlipMembershipProperties;
import com.klipwallet.membership.dto.OneTimeAction;
import com.klipwallet.membership.entity.OperatorInvitation;
import com.klipwallet.membership.service.OperatorInvitable;

import static com.klipwallet.membership.config.SecurityConfig.ROLE_KLIP_KAKAO;

@RequiredArgsConstructor
@Slf4j
public class KlipMembershipOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final KlipMembershipProperties properties;
    private final OperatorInvitable operatorInvitable;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        try {
            if (isAdmin()) {
                response.sendRedirect(properties.getAdminFrontUrl());
                return;
            }
            if (isKakao(authentication)) {
                CodeAndAction codeAndAction = getCodeAndAction(request);
                OneTimeAction action = codeAndAction.action();
                String invitationCode = codeAndAction.code();
                switch (action) {
                    case NONE -> log.warn("OneTimeAction is none. {}", request);
                    case INVITE_OPERATOR -> tryInviteOperator(request, response, authentication, invitationCode);
                    // TODO @Jordan case JOIN_OPENCHATTING -> tryJoinOpenChatting();
                    default -> log.error("OneTimeAction is invalid. {}", request);
                }
            }
            if (response.isCommitted()) {
                return;
            }
            response.sendRedirect(properties.getToolFrontUrl());
        } catch (Exception cause) {
            log.error("Error onAuthenticationSuccess.", cause);
            response.sendRedirect("%s/landing/invite-operator/result?status=error&httpStatus=500&code=500000&err=Error".formatted(
                    properties.getToolFrontUrl()));
        }
    }

    private void tryInviteOperator(HttpServletRequest request, HttpServletResponse response, Authentication authentication, String invitationCode)
            throws IOException {
        OperatorInvitation invitation = operatorInvitable.lookup(invitationCode);
        if (isExpired(invitation)) {    // 운영진 초대가 만료.
            response.sendRedirect("%s/landing/invite-operator/result?status=unknown".formatted(properties.getToolFrontUrl()));
        } else if (isAlreadyOperator(authentication)) {    // 이미 운영진인 경우
            response.sendRedirect("%s/landing/invite-operator/result?status=alreadyDone".formatted(properties.getToolFrontUrl()));
        } else {    // 운영진 초대가 가능한 경우: 운영진 초대 페이지로
            // 이후 운영진 가입 API에서 사용하기 위해서 저장한다.
            request.getSession().setAttribute(OperatorInvitation.STORE_KEY, invitationCode);
            response.sendRedirect("%s/landing/invite-operator/agreements".formatted(properties.getToolFrontUrl()));
        }
    }

    @NonNull
    private CodeAndAction getCodeAndAction(HttpServletRequest request) {
        String state = request.getParameter("state");
        String[] codes = state.split(":");
        String code = codes[0];
        String otActionString = null;
        if (codes.length > 1) {
            otActionString = codes[1];
        }
        OneTimeAction otAction = OneTimeAction.fromDisplay(otActionString);
        return new CodeAndAction(code, otAction);
    }

    private boolean isExpired(OperatorInvitation invitation) {
        return invitation == null;
    }

    private boolean isKakao(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_KLIP_KAKAO));
    }

    private boolean isAlreadyOperator(Authentication authentication) {
        KlipMembershipOAuth2User user = (KlipMembershipOAuth2User) authentication.getPrincipal();
        String kakaoUserId = user.getName();
        return operatorInvitable.isAlreadyOperator(kakaoUserId);
    }

    private boolean isAdmin() {
        return KlipMembershipOAuth2UserService.isAdmin();
    }

    private record CodeAndAction(String code, OneTimeAction action) {

    }
}

package com.klipwallet.membership.config.security;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.exception.BaseException;
import com.klipwallet.membership.exception.NotFoundException;
import com.klipwallet.membership.service.AdminService;
import com.klipwallet.membership.service.PartnerService;

import static com.klipwallet.membership.config.SecurityConfig.CLIENT_ID_GOOGLE;

@RequiredArgsConstructor
public class KlipMembershipOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    /**
     * membership-admin-api 도메인 시작 문자열
     * <p>
     * <b>매우 중요!!!</b>
     * 만약 Klip Membership Admin의 도메인이 본 상수로 시작하지 않으면, Tool과 Admin 인증 분기 처리를 할 수 없게됨.
     * </p>
     */
    public static final String PREFIX_MEMBERSHIP_ADMIN_API = "membership-admin-api.";
    private final DefaultOAuth2UserService defaultImpl = new DefaultOAuth2UserService();
    private final PartnerService partnerService;
    private final AdminService adminService;

    static boolean isAdmin() {
        HttpServletRequest request = getRequest();
        try {
            URL url = new URL(request.getRequestURL().toString());
            return url.getHost().startsWith(PREFIX_MEMBERSHIP_ADMIN_API);
        } catch (MalformedURLException e) {
            throw new BaseException(e); // 발생 가능성 0%
        }
    }

    @NonNull
    private static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    @Override
    public KlipMembershipOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        if (!isGoogle(userRequest)) {
            return null;
        }
        OAuth2User oauth2User = defaultImpl.loadUser(userRequest);
        try {
            if (isAdmin()) {  // Klip Membership Admin 인증
                return signInToAdmin(oauth2User);
            }
            // Klip Membership Tool 인증
            return signInToTool(oauth2User);
        } catch (NotFoundException cause) { // 멤버가 존재하지 않으면 비회원으로 인증
            return KlipMembershipOAuth2User.notMemberOnGoogle(oauth2User, userRequest);
        }
    }

    private boolean isGoogle(OAuth2UserRequest userRequest) {
        // spring.security.oauth2.client.registration.google
        return userRequest.getClientRegistration().getRegistrationId().equals(CLIENT_ID_GOOGLE);
    }

    @NonNull
    private KlipMembershipOAuth2User signInToAdmin(OAuth2User oauth2User) {
        String oauthId = oauth2User.getName();
        String email = KlipMembershipOAuth2User.getGoogleEmail(oauth2User);
        Admin admin = adminService.signIn(email, oauthId);
        return KlipMembershipOAuth2User.adminOnGoogle(admin, oauth2User);
    }

    @NonNull
    private KlipMembershipOAuth2User signInToTool(OAuth2User oauth2User) {
        String oauthId = oauth2User.getName();
        Partner partner = partnerService.signIn(oauthId);
        return KlipMembershipOAuth2User.partnerOnGoogle(partner, oauth2User);
    }
}

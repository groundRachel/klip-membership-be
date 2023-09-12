package com.klipwallet.membership.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static com.klipwallet.membership.config.SecurityConfig.CLIENT_ID_KAKAO;

@RequiredArgsConstructor
public class KakaoOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService defaultImpl = new DefaultOAuth2UserService();

    @Override
    public KlipMembershipOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        if (!isKakao(userRequest)) {
            return null;
        }
        OAuth2User oauth2User = defaultImpl.loadUser(userRequest);
        return KlipMembershipOAuth2User.kakao(oauth2User, userRequest.getAccessToken());
    }


    private boolean isKakao(OAuth2UserRequest userRequest) {
        // spring.security.oauth2.client.registration.kakao
        return userRequest.getClientRegistration().getRegistrationId().equals(CLIENT_ID_KAKAO);
    }
}

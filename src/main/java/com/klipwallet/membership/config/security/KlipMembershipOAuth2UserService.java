package com.klipwallet.membership.config.security;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class KlipMembershipOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService defaultImpl = new DefaultOAuth2UserService();

    @Override
    public KlipMembershipOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = defaultImpl.loadUser(userRequest);
        // FIXME @Jordan Member, Partner 엔티티 별 PARTNER or ADMIN or SUPER_ADMIN 권한 설정!
        return KlipMembershipOAuth2User.memberOnGoogle(oauth2User);
    }

    /**
     * {@link DefaultOAuth2UserService#loadUser(org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest)}
     * 에서 미리 검증되므로 여기에서는 강력하게 유효성 체크할 필요 없음
     *
     * @see #defaultImpl
     */
    private String getUserNameAttributeName(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                          .getUserNameAttributeName();
    }
}

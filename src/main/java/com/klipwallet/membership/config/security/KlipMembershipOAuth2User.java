package com.klipwallet.membership.config.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;

import static com.klipwallet.membership.config.SecurityConfig.*;
import static java.util.Collections.emptyMap;

@ToString
@EqualsAndHashCode
public class KlipMembershipOAuth2User implements AuthenticatedUser, Serializable {
    @Serial
    private static final long serialVersionUID = 9102776982135701748L;
    @Nullable
    private final MemberId memberId;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    private final String email;
    private final String kakaoPhoneNumber;
    @Nullable
    @Getter
    private final OAuth2AccessToken kakaoAccessToken;

    @JsonCreator
    public KlipMembershipOAuth2User(@JsonProperty("memberId") @Nullable MemberId memberId,
                                    @JsonProperty("attributes") Map<String, Object> attributes,
                                    @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
                                    @JsonProperty("name") String name,
                                    @JsonProperty("email") String email,
                                    @JsonProperty("kakaoPhoneNumber") String kakaoPhoneNumber,
                                    @JsonProperty("kakaoAccessToken") @Nullable OAuth2AccessToken kakaoAccessToken) {
        this.memberId = memberId;
        this.attributes = attributes;
        this.authorities = authorities;
        this.name = name;
        this.email = email;
        this.kakaoPhoneNumber = kakaoPhoneNumber;
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public KlipMembershipOAuth2User(@Nullable MemberId memberId,
                                    Collection<? extends GrantedAuthority> authorities,
                                    String name, String email) {
        this(memberId, emptyMap(), authorities, name, email, null, null);
    }

    public KlipMembershipOAuth2User(@Nullable MemberId memberId,
                                    Map<String, Object> attributes,
                                    Collection<? extends GrantedAuthority> authorities,
                                    String name, String email) {
        this(memberId, attributes, authorities, name, email, null, null);
    }

    @SuppressWarnings("unused")
    static KlipMembershipOAuth2User notMemberOnGoogle(OAuth2User googleUser, OAuth2UserRequest userRequest) {
        return new KlipMembershipOAuth2User(null, googleUser.getAttributes(), googleUser.getAuthorities(), googleUser.getName(),
                                            getGoogleEmail(googleUser), null, null);
    }

    private static String getGoogleEmail(Map<String, Object> attributes) {
        return (String) attributes.getOrDefault("email", null);
    }

    public static String getGoogleEmail(OAuth2User googleUser) {
        return getGoogleEmail(googleUser.getAttributes());
    }

    public static String getKakaoEmail(OAuth2User kakaoUser) {
        return getKakaoEmail(kakaoUser.getAttributes());
    }

    private static String getKakaoEmail(Map<String, Object> attributes) {
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.getOrDefault("kakao_account", emptyMap());
        return (String) kakaoAccount.get("email");
    }

    private static String getKakaoPhoneNumber(OAuth2User kakaoUser) {
        return getKakaoPhoneNumber(kakaoUser.getAttributes());
    }

    private static String getKakaoPhoneNumber(Map<String, Object> attributes) {
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.getOrDefault("kakao_account", emptyMap());
        return (String) kakaoAccount.get("phone_number");
    }


    static KlipMembershipOAuth2User partnerOnGoogle(Partner partner, OAuth2User googleUser) {
        return new KlipMembershipOAuth2User(partner.getMemberId(), AuthorityUtils.createAuthorityList(ROLE_PARTNER),
                                            googleUser.getName(), getGoogleEmail(googleUser));
    }

    static KlipMembershipOAuth2User adminOnGoogle(Admin admin, OAuth2User googleUser) {
        List<GrantedAuthority> authorities = getAuthorities(admin);
        return new KlipMembershipOAuth2User(admin.getMemberId(), authorities,
                                            googleUser.getName(), getGoogleEmail(googleUser));
    }

    static KlipMembershipOAuth2User kakao(OAuth2User kakaoUser, OAuth2AccessToken accessToken) {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(ROLE_KLIP_KAKAO);
        return new KlipMembershipOAuth2User(null, emptyMap(), authorities,
                                            kakaoUser.getName(), getKakaoPhoneNumber(kakaoUser), getKakaoEmail(kakaoUser),
                                            accessToken);
    }

    @NonNull
    private static List<GrantedAuthority> getAuthorities(Admin admin) {
        if (admin.isSuper()) {  // 슈퍼 어드민
            return AuthorityUtils.createAuthorityList(ROLE_SUPER_ADMIN, ROLE_ADMIN);
        }
        // 일반 어드민
        return AuthorityUtils.createAuthorityList(ROLE_ADMIN);
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getEmail() {
        return this.email;
    }

    @Nullable
    @Override
    public MemberId getMemberId() {
        return this.memberId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Nullable
    @Override
    public String getKakaoPhoneNumber() {
        return this.kakaoPhoneNumber;
    }
}

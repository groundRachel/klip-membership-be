package com.klipwallet.membership.dto.member;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Member;
import com.klipwallet.membership.entity.MemberId;

import static com.klipwallet.membership.config.SecurityConfig.*;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * 멤버 인증 DTO
 *
 * @param isAuthenticated 인증 여부
 * @param isMember        멤버 여부
 * @param profile       프로필 정보. 멤버인 경우 존재
 * @param authorities   권한 목록. 인증된 경우 존재
 */
@Schema(description = "멤버 인증 DTO")
@JsonInclude(Include.NON_NULL)
public record MemberAuthentication(
        @Schema(description = "인증 여부", requiredMode = REQUIRED)
        boolean isAuthenticated,
        @Schema(description = "멤버 여부", requiredMode = REQUIRED)
        boolean isMember,
        @Schema(description = "프로필. 멤버인 경우 존재")
        Profile profile,
        @Schema(description = "권한 목록. 인증된 경우 존재 하며 미인증이면 빈 배열([]) 반환", requiredMode = REQUIRED, example = "[\"" + ROLE_PARTNER + "\"]",
                requiredProperties = {ROLE_PARTNER, ROLE_ADMIN, ROLE_SUPER_ADMIN, OAUTH2_USER, ROLE_KLIP_KAKAO})
        @NonNull Collection<String> authorities
) {

    /**
     * 인증 없음.
     */
    public static final MemberAuthentication NO_AUTH = new MemberAuthentication(false, false, null, emptyList());
    /**
     * 카카오 인증 (실질적으로 인증 없음)
     */
    public static final MemberAuthentication KAKAO = new MemberAuthentication(false, false, null, List.of(ROLE_KLIP_KAKAO));
    public static final MemberAuthentication GOOGLE = new MemberAuthentication(true, false, null, List.of(OAUTH2_USER));

    public MemberAuthentication(@NonNull Member member, @NonNull AuthenticatedUser user) {
        this(true, true, new Profile(member), getAuthorities(user));
    }

    private static Collection<String> getAuthorities(@NonNull AuthenticatedUser user) {
        return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toUnmodifiableSet());
    }


    /**
     * 멤버 프로필 정보
     *
     * @param memberId 멤버 아이디
     * @param name     멤버 이름. 관리자: 이메일 LocalPart, 파트너: 회사명
     * @param email    계정 이메일
     */
    @Schema(description = "멤버 프로필 DTO. 멤버인 경우에만 노출 가능")
    public record Profile(
            @Schema(description = "멤버 아이디", requiredMode = REQUIRED, example = "9")
            @NonNull MemberId memberId,
            @Schema(description = "멤버 이름. 관리자: 이메일 LocalPart, 파트너: 회사명", requiredMode = REQUIRED, example = "jordan.jung")
            @NonNull String name,
            @Schema(description = "이메일", requiredMode = REQUIRED, example = "jordan.jung@groundx.xyz")
            @NonNull String email) {

        @SuppressWarnings("DataFlowIssue")
        public Profile(Member member) {
            this(member.getMemberId(), member.getName(), member.getEmail());
        }
    }
}

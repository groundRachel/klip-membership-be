package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import com.klipwallet.membership.adaptor.jpa.ForJpa;

/**
 * 어드민 Entity
 */
@Entity
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Admin extends Member {
    @ForJpa
    protected Admin() {
    }

    public Admin(String email, MemberId creator) {
        setEmail(email);
        setName(toLocalPart(email));
        createBy(creator);
    }

    private String toLocalPart(String email) {
        return StringUtils.substringBefore(email, "@");
    }

    public boolean isSignUp() {
        return getOAuthId() != null;
    }

    /**
     * 회원가입
     * <p>
     * 어드민은 최초 추가만 되어 있는 상태이며, 최초 인증을 한 번 해야지 회원가입이 완료됨.
     * </p>
     *
     * @param oAuth2Id OAuth2 ID
     */
    @SuppressWarnings("DataFlowIssue")
    public void signUp(String oAuth2Id) {
        setOAuthId(oAuth2Id);
        updateBy(getMemberId());    // 나 자신이 접근한 것이므로 내가 updater
    }
}

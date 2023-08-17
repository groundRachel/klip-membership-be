package com.klipwallet.membership.config.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;

/**
 * {@link org.springframework.data.jpa.repository.config.EnableJpaAuditing} 을 위한 설정
 *
 * @see org.springframework.data.annotation.CreatedBy
 * @see org.springframework.data.annotation.LastModifiedBy
 * @see com.klipwallet.membership.entity.MemberId
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
public class MemberIdAuditorProvider implements AuditorAware<MemberId> {
    @SuppressWarnings("NullableProblems")
    @Override
    public Optional<MemberId> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContextHolderStrategy())
                       .map(SecurityContextHolderStrategy::getContext)
                       .map(SecurityContext::getAuthentication)
                       .filter(Authentication::isAuthenticated)
                       .map(Authentication::getPrincipal)
                       .map(AuthenticatedUser.class::cast)
                       .map(AuthenticatedUser::getMemberId);
    }
}

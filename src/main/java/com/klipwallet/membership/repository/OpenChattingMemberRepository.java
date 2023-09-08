package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.OpenChattingMember;
import com.klipwallet.membership.entity.OpenChattingMember.Role;

public interface OpenChattingMemberRepository extends JpaRepository<OpenChattingMember, Long> {
    Long countByOperatorIdAndRole(Long operatorId, Role role);
}

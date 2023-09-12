package com.klipwallet.membership.repository;

import java.util.Optional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.OpenChattingMember;
import com.klipwallet.membership.entity.OpenChattingMember.Role;

public interface OpenChattingMemberRepository extends JpaRepository<OpenChattingMember, Long> {
    Long countByOperatorIdAndRole(Long operatorId, Role role);

    /**
     * openChattingId와 klipId로 OpenChattingMember 조회
     *
     * @param openChattingId openChattingId
     * @param klipId         klipId
     * @return OpenChattingMember
     */
    Optional<OpenChattingMember> findByOpenChattingIdAndKlipId(Long openChattingId, Long klipId);

    List<OpenChattingMember> findByOpenChattingIdAndRole(Long openChattingId, Role role);
}

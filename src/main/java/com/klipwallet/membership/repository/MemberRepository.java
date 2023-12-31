package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {

}

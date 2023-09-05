package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Operator;

public interface OperatorRepository extends JpaRepository<Operator, Long> {
    /**
     * 카카오아이디로 가입된 운영진이 존재하는가?
     *
     * @param kakaoUserId 카카오 아이디
     * @return 운영진 존재 여부
     */
    boolean existsByKakaoUserId(String kakaoUserId);
}

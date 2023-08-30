package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Operator;

public interface OperatorRepository extends JpaRepository<Operator, Long> {
}

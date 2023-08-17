package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Faq;

public interface FaqRepository extends JpaRepository<Faq, Integer> {

}

package com.klipwallet.membership.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void insertAndSelect() {
        Member entity = new Member();
        Member saved = memberRepository.save(entity);
        memberRepository.flush();

        em.flush();
        em.clear();

        Member findUser = memberRepository.findById(saved.getId())
                                          .orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(saved).isEqualTo(findUser);
    }
}
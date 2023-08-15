package com.klipwallet.membership.repository;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.entity.Member;
import com.klipwallet.membership.entity.MemberId;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @WithAuthenticatedUser(memberId = 2)
    @Test
    void insertAndSelect() {
        Member entity = new Member();
        Member saved = memberRepository.save(entity);
        memberRepository.flush();

        em.flush();
        em.clear();
        assertThat(saved.getMemberId()).isNotNull();

        Member findUser = memberRepository.findById(saved.getId()).orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(findUser.getMemberId()).isNotNull();
        assertThat(findUser.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(findUser.getCreatedBy()).isEqualTo(new MemberId(2));
        assertThat(findUser.getUpdatedAt()).isBefore(LocalDateTime.now());
        assertThat(findUser.getUpdatedBy()).isEqualTo(new MemberId(2));
        assertThat(saved).isEqualTo(findUser);
    }
}
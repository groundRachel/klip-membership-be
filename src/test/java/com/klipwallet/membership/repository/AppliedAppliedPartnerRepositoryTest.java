package com.klipwallet.membership.repository;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PartnerRepositoryTest {
    @Autowired
    PartnerRepository partnerRepository;

    @PersistenceContext
    EntityManager em;

    @WithAuthenticatedUser(memberId = 2)
    @Test
    void insertAndSelect() {
        Partner entity = new Partner();
        Partner saved = partnerRepository.save(entity);
        partnerRepository.flush();

        em.flush();
        em.clear();
        assertThat(saved.getMemberId()).isNotNull();

        Partner findUser = partnerRepository.findById(saved.getId())
                                            .orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(findUser.getMemberId()).isNotNull();
        assertThat(findUser.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(findUser.getCreatedBy()).isEqualTo(new MemberId(2));
        assertThat(findUser.getUpdatedAt()).isBefore(LocalDateTime.now());
        assertThat(findUser.getUpdatedBy()).isEqualTo(new MemberId(2));
        assertThat(saved).isEqualTo(findUser);
    }
}
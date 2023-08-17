package com.klipwallet.membership.repository;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.AppliedPartner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppliedAppliedPartnerRepositoryTest {
    @Autowired
    AppliedPartnerRepository appliedPartnerRepository;

    @PersistenceContext
    EntityManager em;

    @WithAuthenticatedUser(memberId = 2)
    @Test
    void insertAndSelect() {
        AppliedPartner entity = new AppliedPartner();
        AppliedPartner saved = appliedPartnerRepository.save(entity);
        appliedPartnerRepository.flush();

        em.flush();
        em.clear();
        assertThat(saved.getMemberId()).isNotNull();

        AppliedPartner findUser = appliedPartnerRepository.findById(saved.getId())
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
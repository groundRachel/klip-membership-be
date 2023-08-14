package com.klipwallet.membership.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.entity.AppliedPartner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppliedAppliedPartnerRepositoryTest {
    @Autowired
    AppliedPartnerRepository appliedPartnerRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void insertAndSelect() {
        AppliedPartner entity = new AppliedPartner();
        AppliedPartner saved = appliedPartnerRepository.save(entity);
        appliedPartnerRepository.flush();

        em.flush();
        em.clear();

        AppliedPartner findUser = appliedPartnerRepository.findById(saved.getId())
                                                          .orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(saved).isEqualTo(findUser);
    }
}
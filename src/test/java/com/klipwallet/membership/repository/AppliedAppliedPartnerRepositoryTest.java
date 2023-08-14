package com.klipwallet.membership.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.entity.Partner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PartnerRepositoryTest {
    @Autowired
    PartnerRepository partnerRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void insertAndSelect() {
        Partner entity = new Partner();
        Partner saved = partnerRepository.save(entity);
        partnerRepository.flush();

        em.flush();
        em.clear();

        Partner findUser = partnerRepository.findById(saved.getId())
                                            .orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(saved).isEqualTo(findUser);
    }
}
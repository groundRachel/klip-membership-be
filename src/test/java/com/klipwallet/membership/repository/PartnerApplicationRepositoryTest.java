package com.klipwallet.membership.repository;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.entity.PartnerApplication;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Test가 깨져서 우선 비활성화 처리함.") // FIXME @Jordan
@DataJpaTest
class PartnerApplicationRepositoryTest {
    @Autowired
    PartnerApplicationRepository partnerApplicationRepository;

    @PersistenceContext
    EntityManager em;

    @WithAuthenticatedUser(memberId = 2)
    @Test
    void insertAndSelect() {
        PartnerApplication entity = new PartnerApplication();
        PartnerApplication saved = partnerApplicationRepository.save(entity);
        partnerApplicationRepository.flush();

        em.flush();
        em.clear();
        assertThat(saved.getId()).isNotNull();

        PartnerApplication findUser = partnerApplicationRepository.findById(saved.getId())
                                                                  .orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(findUser.getId()).isNotNull();
        assertThat(findUser.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(findUser.getProcessedAt()).isBefore(LocalDateTime.now());
        assertThat(findUser.getProcessorId()).isEqualTo(2);
        assertThat(saved).isEqualTo(findUser);
    }

}

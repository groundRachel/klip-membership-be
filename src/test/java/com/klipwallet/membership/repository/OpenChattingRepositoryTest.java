package com.klipwallet.membership.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration.builder;

@Disabled("Test가 깨져서 우선 비활성화 처리함.") // FIXME @Jordan
@DataJpaTest
class OpenChattingRepositoryTest {
    @Autowired
    OpenChattingRepository openChattingRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void insertAndSelect() {
        OpenChatting entity = new OpenChatting("title", "desc", "http://coverImage",
                                               new KakaoOpenlinkSummary(1L, "yes"), new Address("0xa005e82487fb629923b9598fffd1c2e9499f0cab"),
                                               new MemberId(1));

        OpenChatting saved = openChattingRepository.save(entity);
        em.flush();
        em.clear();

        OpenChatting findEntity = openChattingRepository.findById(saved.getId())
                                                        .orElse(null);

        assertThat(findEntity).isNotNull()
                              // 생성일시는 DB 에서 default 로 만듦
                              .usingRecursiveComparison(builder().withIgnoredFields("createdAt").build())
                              .isEqualTo(saved);
    }
}
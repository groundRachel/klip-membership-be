package com.klipwallet.membership.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.klipwallet.membership.entity.OpenChattingMember;

import static com.klipwallet.membership.entity.OpenChattingMember.Role.NFT_HOLDER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OpenChattingMemberRepositoryTest {
    @Autowired
    OpenChattingMemberRepository openChattingMemberRepository;

    @AfterEach
    void afterEach() {
        openChattingMemberRepository.deleteAll();
        openChattingMemberRepository.flush();
    }

    @Test
    void findByOpenChattingIdAndKlipId() {
        Long openChattingId = 1L;
        Long klipId = 1L;
        OpenChattingMember entity = new OpenChattingMember(openChattingId, klipId, "123131231", 1L, "testname", "http://coverImage", NFT_HOLDER);

        OpenChattingMember saved = openChattingMemberRepository.save(entity);
        OpenChattingMember findEntity = openChattingMemberRepository.findByOpenChattingIdAndKlipId(openChattingId, klipId)
                                                .orElse(null);

        assertThat(findEntity).isNotNull().isEqualTo(saved);
    }
}
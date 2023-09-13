package com.klipwallet.membership.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChattingNft;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OpenChattingNftRepositoryTest {
    @Autowired
    OpenChattingNftRepository openChattingNftRepository;
    @PersistenceContext
    EntityManager em;

    @AfterEach
    void afterEach() {
        openChattingNftRepository.deleteAll();
        openChattingNftRepository.flush();
    }

    @Test
    void findByKlipDropsScaAndDropId() {
        Long openChattingId = 1L;
        Long dropId = 1L;
        MemberId memberId = new MemberId(1);
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        OpenChattingNft entity = new OpenChattingNft(openChattingId, dropId, contractAddress, memberId);

        OpenChattingNft saved = openChattingNftRepository.save(entity);
        OpenChattingNft findEntity = openChattingNftRepository.findByScaAndDropId(contractAddress, dropId)
                                                .orElse(null);

        assertThat(findEntity).isNotNull().isEqualTo(saved);
    }
}
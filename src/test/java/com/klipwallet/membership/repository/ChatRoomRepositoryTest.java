package com.klipwallet.membership.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.ChatRoom;
import com.klipwallet.membership.entity.kakao.OpenChatRoomId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration.builder;

@DataJpaTest
class ChatRoomRepositoryTest {
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void insertAndSelect() {
        ChatRoom entity = new ChatRoom(new OpenChatRoomId("1"), "title", "http://coverImage",
                                       new Address("0xa005e82487fb629923b9598fffd1c2e9499f0cab"), 1);

        ChatRoom saved = chatRoomRepository.save(entity);
        em.flush();
        em.clear();

        ChatRoom findEntity = chatRoomRepository.findById(saved.getId())
                                                .orElse(null);

        assertThat(findEntity).isNotNull()
                              // 생성일시는 DB 에서 default 로 만듦
                              .usingRecursiveComparison(builder().withIgnoredFields("createdAt").build())
                              .isEqualTo(saved);
    }
}
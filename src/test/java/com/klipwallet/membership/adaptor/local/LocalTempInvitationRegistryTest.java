package com.klipwallet.membership.adaptor.local;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OperatorInvitation;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LocalTempInvitationRegistryTest {
    LocalTempInvitationRegistry localTempInvitationRegistry;

    @BeforeEach
    void setUp() throws IOException {
        localTempInvitationRegistry = new LocalTempInvitationRegistry(new ObjectMapper());
    }

    @Test
    void save() {
        // given
        MemberId partnerId = new MemberId(11);
        String phoneNumber = "0101111222";
        OperatorInvitation invitation = new OperatorInvitation(partnerId, phoneNumber);
        // when
        String uuid = localTempInvitationRegistry.save(invitation);
        // then
        assertThat(uuid).isNotBlank();

        OperatorInvitation result = localTempInvitationRegistry.lookup(uuid);
        assertThat(result).isEqualTo(invitation);
    }
}
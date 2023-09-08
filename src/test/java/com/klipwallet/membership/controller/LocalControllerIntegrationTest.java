package com.klipwallet.membership.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithPartnerUser;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class LocalControllerIntegrationTest {

    @WithPartnerUser(memberId = 9)
    @Test
    void inviteOperator(@Autowired MockMvc mvc) throws Exception {
        var ra =
                mvc.perform(post("/tool/v1/operators/invite-local")
                                    .param("phone", "01026383987"))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.invitationUrl").value(startsWith("http")))
                   .andReturn();
    }
}
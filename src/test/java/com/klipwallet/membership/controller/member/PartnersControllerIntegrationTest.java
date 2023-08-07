package com.klipwallet.membership.controller.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PartnersControllerIntegrationTest {
    @Test
    void apply(@Autowired MockMvc mvc) throws Exception {
        String requestJson = """
                             {
                               "name": "(주) 그라운드엑스",
                               "phoneNumber": "010-1234-5678",
                               "businessRegistrationNumber": "000-00-00000",
                               "email": "example@groundx.xyz",
                               "oAuthID": "292085223830.apps.googleusercontent.com"
                             }
                             """;
        mvc.perform(post("/tool/partners/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").exists())
           .andExpect(jsonPath("$.createdAt").exists())
           .andExpect(jsonPath("$.updatedAt").exists());
    }
}

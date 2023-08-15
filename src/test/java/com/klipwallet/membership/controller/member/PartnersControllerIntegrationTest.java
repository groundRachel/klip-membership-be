package com.klipwallet.membership.controller.member;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithAuthenticatedUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PartnersControllerIntegrationTest {
    @WithAuthenticatedUser(authorities = "OAUTH2_USER")
    @DisplayName("파트너 가입 요청 성공")
    @Test
    void apply(@Autowired MockMvc mvc) throws Exception {
        String requestJson = """
                             {
                               "name": "(주) 그라운드엑스",
                               "phoneNumber": "010-1234-5678",
                               "businessRegistrationNumber": "000-00-00000",
                               "email": "example@groundx.xyz",
                               "oAuthId": "292085223830.apps.googleusercontent.com"
                             }
                             """;
        mvc.perform(post("/tool/partners/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").exists())
           .andExpect(jsonPath("$.name").value("(주) 그라운드엑스"))
           .andExpect(jsonPath("$.createdAt").exists())
           .andExpect(jsonPath("$.updatedAt").exists());
    }

    @WithAuthenticatedUser
    @DisplayName("파트너 가입 요청을 했지만 권한이 OAUTH2_USER 권한이 없으면: 403")
    @Test
    void applyOnPartner(@Autowired MockMvc mvc) throws Exception {
        String requestJson = """
                             {
                               "name": "(주) 그라운드엑스",
                               "phoneNumber": "010-1234-5678",
                               "businessRegistrationNumber": "000-00-00000",
                               "email": "example@groundx.xyz",
                               "oAuthId": "292085223830.apps.googleusercontent.com"
                             }
                             """;
        mvc.perform(post("/tool/partners/apply")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
           .andExpect(status().isForbidden());
        //           .andExpect(jsonPath("$.code").value(1403))
        //           .andExpect(jsonPath("$.err").value("권한이 부족합니다. OAUTH2_USER"))
    }
}

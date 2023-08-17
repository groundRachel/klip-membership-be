package com.klipwallet.membership.controller.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.dto.member.PartnerDto;
import com.klipwallet.membership.entity.AcceptedPartner;
import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.repository.AcceptedPartnerRepository;
import com.klipwallet.membership.repository.AppliedPartnerRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PartnersAdminControllerTest {

    @Autowired
    AppliedPartnerRepository appliedPartnerRepository;
    @Autowired
    AcceptedPartnerRepository acceptedPartnerRepository;


    @WithAdminUser
    @Test
    void getAppliedPartners(@Autowired MockMvc mvc) {
        // TODO
    }

    @WithAdminUser
    @Test
    void getAcceptedPartners(@Autowired MockMvc mvc) {
        // TODO
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 존재하지 않는 파트너 ID > 404")
    @Test
    void acceptResult_throwExceptionToNotFound(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "id": 999,
                        "accept": "accepted"
                      }
                      """;
        mvc.perform(post("/admin/v1/partners/accept")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(404_002))
           .andExpect(jsonPath("$.err").value("파트너 정보를 조회할 수 없습니다. ID: %d".formatted(999)));
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 이미 승인 처리한 ID > 400")
    @Test
    void acceptResult_throwAlreadyProcessedToBadRequest(@Autowired MockMvc mvc) throws Exception {
        // given
        AppliedPartner apply = new AppliedPartner("(주) 그라운드엑스", "010-1234-5678", "100-00-00001", "example1@groundx.xyz",
                                                  "192085223830.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00001").getId();

        String body = """
                      {
                        "id": %d,
                        "accept": "accepted"
                      }
                      """.formatted(id);
        mvc.perform(post("/admin/v1/partners/accept")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk());

        // when, then
        mvc.perform(post("/admin/v1/partners/accept")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_001));
        //           .andExpect(jsonPath("$.err").value("이미 처리된 요청입니다. ID: %d, 처리상태: accepted,".formatted(id)));
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 승인 > 200")
    @Test
    void acceptResult_status_ACCEPTED(@Autowired MockMvc mvc) throws Exception {
        // given
        AppliedPartner apply = new AppliedPartner("(주) 그라운드엑스", "010-1234-5678", "100-00-00002", "example2@groundx.xyz",
                                                  "292085223830.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00002").getId();

        // when, then
        String body = """
                      {
                        "id": %d,
                        "accept": "accepted"
                      }
                      """.formatted(id);
        mvc.perform(post("/admin/v1/partners/accept")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("(주) 그라운드엑스"));

        AppliedPartner appliedPartner = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00002");
        assertThat(appliedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(appliedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(appliedPartner.getEmail()).isEqualTo("example2@groundx.xyz");
        assertThat(appliedPartner.getOAuthId()).isEqualTo("292085223830.apps.googleusercontent.com");
        assertThat(appliedPartner.getStatus()).isEqualTo(Status.ACCEPTED);

        AcceptedPartner acceptedPartner = acceptedPartnerRepository.findByBusinessRegistrationNumber("100-00-00002");
        assertThat(acceptedPartner).isNotNull();
        assertThat(acceptedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(acceptedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(acceptedPartner.getEmail()).isEqualTo("example2@groundx.xyz");
        assertThat(acceptedPartner.getOAuthId()).isEqualTo("292085223830.apps.googleusercontent.com");
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 거절 > 200")
    @Test
    void acceptResult_status_DECLINED(@Autowired MockMvc mvc) throws Exception {
        // given
        AppliedPartner apply = new AppliedPartner("(주) 그라운드엑스", "010-1234-5678", "100-00-00003", "example3@groundx.xyz",
                                                  "392085223830.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00003").getId();

        // when, then
        String body = """
                      {
                        "id": %d,
                        "accept": "declined",
                        "declineReason": "정상적이지 않은 사업자번호입니다."
                      }
                      """.formatted(id);
        mvc.perform(post("/admin/v1/partners/accept")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("(주) 그라운드엑스"));

        AppliedPartner appliedPartner = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00003");
        assertThat(appliedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(appliedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(appliedPartner.getEmail()).isEqualTo("example3@groundx.xyz");
        assertThat(appliedPartner.getOAuthId()).isEqualTo("392085223830.apps.googleusercontent.com");
        assertThat(appliedPartner.getStatus()).isEqualTo(Status.DECLINED);
        assertThat(appliedPartner.getDeclineReason()).isEqualTo("정상적이지 않은 사업자번호입니다.");

        AcceptedPartner acceptedPartner = acceptedPartnerRepository.findByBusinessRegistrationNumber("100-00-00003");
        assertThat(acceptedPartner).isNull();
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 잘못된 status Applied > 400")
    @Test
    void acceptResult_status_APPLIED(@Autowired MockMvc mvc) throws Exception {
        // given
        AppliedPartner apply = new AppliedPartner("(주) 그라운드엑스", "010-1234-5678", "100-00-00004", "example4@groundx.xyz",
                                                  "492085223830.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00004").getId();

        // when, then
        String body = """
                      {
                        "id": %d,
                        "accept": "applied"
                      }
                      """.formatted(id);
        mvc.perform(post("/admin/v1/partners/accept")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_002));
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 잘못된 status undefined > 400")
    @Test
    void acceptResult_status_UNDEFINED(@Autowired MockMvc mvc) throws Exception {
        // given
        AppliedPartner apply = new AppliedPartner("(주) 그라운드엑스", "010-1234-5678", "100-00-00005", "example5@groundx.xyz",
                                                  "592085223830.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00005").getId();

        // when, then
        String body = """
                      {
                        "id": %d,
                        "accept": "undefined_status"
                      }
                      """.formatted(id);
        mvc.perform(post("/admin/v1/partners/accept")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest());
    }
}

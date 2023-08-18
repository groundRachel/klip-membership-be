package com.klipwallet.membership.controller.admin;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.repository.AppliedPartnerRepository;

import static com.klipwallet.membership.exception.ErrorCode.PARTNER_APPLICATION_ALREADY_PROCESSED;
import static com.klipwallet.membership.exception.ErrorCode.PARTNER_APPLICATION_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PartnerApplicationControllerTest {

    @Autowired
    AppliedPartnerRepository appliedPartnerRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @AfterEach
    void afterEach() {
        appliedPartnerRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 존재하지 않는 파트너 ID > 404")
    @Test
    void approveResult_throwExceptionToNotFound(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "id": 999
                      }
                      """;
        mvc.perform(post("/admin/v1/partner-applications/approve")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(PARTNER_APPLICATION_NOT_FOUND.getCode()))
           .andExpect(jsonPath("$.err").value("파트너 지원 정보를 조회할 수 없습니다. ID: %d".formatted(999)));
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 이미 승인 처리한 ID > 400")
    @Test
    void approveResult_throwAlreadyProcessedToBadRequest(@Autowired MockMvc mvc) throws Exception {
        // given
        AppliedPartner apply = new AppliedPartner("(주) 그라운드엑스", "010-1234-5678", "100-00-00001", "exampl-admin-controller1@groundx.xyz",
                                                  "192085223831.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00001").orElseThrow(RuntimeException::new).getId();

        String body = """
                      {
                        "id": %d
                      }
                      """.formatted(id);
        mvc.perform(post("/admin/v1/partner-applications/approve")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk());

        // when, then
        mvc.perform(post("/admin/v1/partner-applications/approve")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isConflict())
           .andExpect(jsonPath("$.code").value(PARTNER_APPLICATION_ALREADY_PROCESSED.getCode()));
        //           .andExpect(jsonPath("$.err").value("이미 처리된 요청입니다. ID: %d, 처리상태: accepted,".formatted(id)));
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 승인 > 200")
    @Test
    void approveResult_status_APPROVED(@Autowired MockMvc mvc) throws Exception {
        // given
        AppliedPartner apply = new AppliedPartner("(주) 그라운드엑스", "010-1234-5678", "100-00-00002", "exampl-admin-controller2@groundx.xyz",
                                                  "292085223831.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00002").orElseThrow(RuntimeException::new).getId();

        // when, then
        String body = """
                      {
                        "id": %d
                      }
                      """.formatted(id);
        mvc.perform(post("/admin/v1/partner-applications/approve")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk());

        AppliedPartner appliedPartner = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00002").orElseThrow(RuntimeException::new);
        assertThat(appliedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(appliedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(appliedPartner.getEmail()).isEqualTo("exampl-admin-controller2@groundx.xyz");
        assertThat(appliedPartner.getOAuthId()).isEqualTo("292085223831.apps.googleusercontent.com");
        assertThat(appliedPartner.getStatus()).isEqualTo(Status.APPROVED);

        Partner partner = partnerRepository.findByBusinessRegistrationNumber("100-00-00002").orElseThrow(RuntimeException::new);
        assertThat(partner).isNotNull();
        assertThat(partner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(partner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(partner.getEmail()).isEqualTo("exampl-admin-controller2@groundx.xyz");
        assertThat(partner.getOAuthId()).isEqualTo("292085223831.apps.googleusercontent.com");
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 거절 > 200")
    @Test
    void approveResult_status_REJECTED(@Autowired MockMvc mvc) throws Exception {
        // given
        AppliedPartner apply = new AppliedPartner("(주) 그라운드엑스", "010-1234-5678", "100-00-00003", "exampl-admin-controller3@groundx.xyz",
                                                  "392085223831.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00003").orElseThrow(RuntimeException::new).getId();

        // when, then
        String body = """
                      {
                        "id": %d,
                        "rejectReason": "정상적이지 않은 사업자번호입니다."
                      }
                      """.formatted(id);
        mvc.perform(post("/admin/v1/partner-applications/reject")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk());

        AppliedPartner appliedPartner = appliedPartnerRepository.findByBusinessRegistrationNumber("100-00-00003").orElseThrow(RuntimeException::new);
        assertThat(appliedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(appliedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(appliedPartner.getEmail()).isEqualTo("exampl-admin-controller3@groundx.xyz");
        assertThat(appliedPartner.getOAuthId()).isEqualTo("392085223831.apps.googleusercontent.com");
        assertThat(appliedPartner.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(appliedPartner.getRejectReason()).isEqualTo("정상적이지 않은 사업자번호입니다.");

        partnerRepository.findByBusinessRegistrationNumber("100-00-00003")
                         .ifPresent(p -> {throw new RuntimeException();});
    }
}

package com.klipwallet.membership.controller.admin;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static com.klipwallet.membership.entity.PartnerApplication.Status.APPROVED;
import static com.klipwallet.membership.exception.ErrorCode.PARTNER_APPLICATION_ALREADY_PROCESSED;
import static com.klipwallet.membership.exception.ErrorCode.PARTNER_APPLICATION_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PartnerApplicationAdminControllerTest {

    @Autowired
    PartnerApplicationRepository partnerApplicationRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @AfterEach
    void afterEach() {
        partnerApplicationRepository.deleteAll();
        partnerApplicationRepository.flush();
        partnerRepository.deleteAll();
        partnerRepository.flush();
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 존재하지 않는 파트너 ID > 404")
    @Test
    void approveResult_throwExceptionToNotFound(@Autowired MockMvc mvc) throws Exception {

        mvc.perform(post("/admin/v1/partner-applications/{0}/approve", 999)
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(PARTNER_APPLICATION_NOT_FOUND.getCode()))
           .andExpect(jsonPath("$.err").value("파트너 신청 정보를 조회할 수 없습니다. ID: %d".formatted(999)));
    }

    @WithAdminUser(memberId = 23)
    @DisplayName("파트너 가입 승인: 이미 승인 처리한 ID > 400")
    @Test
    void approveResult_throwAlreadyProcessedToBadRequest(@Autowired MockMvc mvc) throws Exception {
        // given
        PartnerApplication apply = new PartnerApplication("(주) 그라운드엑스", "010-1234-5678", "100-00-00001", "exampl-admin-controller1@groundx.xyz",
                                                          "192085223831.apps.googleusercontent.com", new MemberId(23));
        partnerApplicationRepository.save(apply);
        partnerApplicationRepository.flush();

        Integer id = partnerApplicationRepository.findByBusinessRegistrationNumber("100-00-00001").orElseThrow().getId();

        mvc.perform(post("/admin/v1/partner-applications/{0}/approve", id)
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isOk());

        // when, then
        mvc.perform(post("/admin/v1/partner-applications/{0}/approve", id)
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isConflict())
           .andExpect(jsonPath("$.code").value(PARTNER_APPLICATION_ALREADY_PROCESSED.getCode()))
           .andExpect(jsonPath("$.err", containsString("이미 처리된 파트너 가입 요청입니다. ID: %d, 처리상태: %s, 처리자: %d,".formatted(id, APPROVED.toDisplay(), 23))));
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 승인 > 200")
    @Test
    void approveResult_status_APPROVED(@Autowired MockMvc mvc) throws Exception {
        // given
        PartnerApplication apply = new PartnerApplication("(주) 그라운드엑스", "010-1234-5678", "100-00-00002", "exampl-admin-controller2@groundx.xyz",
                                                          "292085223831.apps.googleusercontent.com", new MemberId(23));
        partnerApplicationRepository.save(apply);
        partnerApplicationRepository.flush();

        Integer id = partnerApplicationRepository.findByBusinessRegistrationNumber("100-00-00002").orElseThrow().getId();

        // when, then
        mvc.perform(post("/admin/v1/partner-applications/{0}/approve", id)
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isOk());

        PartnerApplication partnerApplication = partnerApplicationRepository.findByBusinessRegistrationNumber("100-00-00002").orElseThrow();
        assertThat(partnerApplication.getBusinessName()).isEqualTo("(주) 그라운드엑스");
        assertThat(partnerApplication.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(partnerApplication.getEmail()).isEqualTo("exampl-admin-controller2@groundx.xyz");
        assertThat(partnerApplication.getOAuthId()).isEqualTo("292085223831.apps.googleusercontent.com");
        assertThat(partnerApplication.getStatus()).isEqualTo(APPROVED);

        Partner partner = partnerRepository.findByBusinessRegistrationNumber("100-00-00002").orElseThrow();
        assertThat(partner).isNotNull();
        assertThat(partner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(partner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(partner.getEmail()).isEqualTo("exampl-admin-controller2@groundx.xyz");
        assertThat(partner.getOAuthId()).isEqualTo("292085223831.apps.googleusercontent.com");
        assertThat(partner.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(partner.getCreatorId()).isEqualTo(new MemberId(23));
        assertThat(partner.getUpdatedAt()).isBefore(LocalDateTime.now());
        assertThat(partner.getUpdaterId()).isEqualTo(new MemberId(23));
    }

    @WithAdminUser
    @DisplayName("파트너 가입 승인: 거절 > 200")
    @Test
    void approveResult_status_REJECTED(@Autowired MockMvc mvc) throws Exception {
        // given
        PartnerApplication apply = new PartnerApplication("(주) 그라운드엑스", "010-1234-5678", "100-00-00003", "exampl-admin-controller3@groundx.xyz",
                                                          "392085223831.apps.googleusercontent.com", new MemberId(23));
        partnerApplicationRepository.save(apply);
        partnerApplicationRepository.flush();

        Integer id = partnerApplicationRepository.findByBusinessRegistrationNumber("100-00-00003").orElseThrow().getId();

        // when, then
        String body = """
                      {
                        "rejectReason": "정상적이지 않은 사업자번호입니다."
                      }
                      """;
        mvc.perform(post("/admin/v1/partner-applications/{0}/reject", id)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk());

        PartnerApplication partnerApplication = partnerApplicationRepository.findByBusinessRegistrationNumber("100-00-00003").orElseThrow();
        assertThat(partnerApplication.getBusinessName()).isEqualTo("(주) 그라운드엑스");
        assertThat(partnerApplication.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(partnerApplication.getEmail()).isEqualTo("exampl-admin-controller3@groundx.xyz");
        assertThat(partnerApplication.getOAuthId()).isEqualTo("392085223831.apps.googleusercontent.com");
        assertThat(partnerApplication.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(partnerApplication.getRejectReason()).isEqualTo("정상적이지 않은 사업자번호입니다.");

        partnerRepository.findByBusinessRegistrationNumber("100-00-00003")
                         .ifPresent(p -> {throw new RuntimeException();});
    }
}

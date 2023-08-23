package com.klipwallet.membership.controller.admin;

import java.time.LocalDateTime;
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
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static com.klipwallet.membership.entity.PartnerApplication.Status.*;
import static com.klipwallet.membership.exception.ErrorCode.PARTNER_APPLICATION_ALREADY_PROCESSED;
import static com.klipwallet.membership.exception.ErrorCode.PARTNER_APPLICATION_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                                                          "192085223831.apps.googleusercontent.com");
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
                                                          "292085223831.apps.googleusercontent.com");
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
        assertThat(partnerApplication.getProcessedAt()).isBefore(LocalDateTime.now());
        assertThat(partnerApplication.getProcessorId()).isEqualTo(new MemberId(23));

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
                                                          "392085223831.apps.googleusercontent.com");
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
        assertThat(partnerApplication.getProcessedAt()).isBefore(LocalDateTime.now());
        assertThat(partnerApplication.getProcessorId()).isEqualTo(new MemberId(23));

        partnerRepository.findByBusinessRegistrationNumber("100-00-00003")
                         .ifPresent(p -> {throw new RuntimeException();});
    }

    MemberId processor = new MemberId(23);
    List<PartnerApplication> applications = Arrays.asList(
            new PartnerApplication("(주) 그라운드엑스0", "010-1234-5678", "000-00-00001", "example1@groundx.xyz", "192085223830"),
            new PartnerApplication("(주) 그라운드엑스1", "010-1234-5678", "00-00002", "example2@groundx.xyz", "292085223830"),
            new PartnerApplication("(주) 그라운드엑스2", "010-1234-5678", "000-00-00003", "example3@groundx.xyz", "392085223830"),

            new PartnerApplication("(주) 그라운드엑스3", "010-1234-5678", "000-00-00004", "example4@groundx.xyz", "492085223830").approve(processor),
            new PartnerApplication("(주) 그라운드엑스4", "010-1234-5678", "000-00-00005", "example5@groundx.xyz", "592085223830").approve(processor),
            new PartnerApplication("(주) 그라운드엑스5", "010-1234-5678", "000-00-00006", "example6@groundx.xyz", "692085223830").approve(processor),

            new PartnerApplication("(주) 그라운드엑스6", "010-1234-5678", "000-00-00007", "example7@groundx.xyz", "792085223830").reject("", processor),
            new PartnerApplication("(주) 그라운드엑스7", "010-1234-5678", "000-00-00008", "example8@groundx.xyz", "892085223830").reject("", processor),
            new PartnerApplication("(주) 그라운드엑스8", "010-1234-5678", "000-00-00009", "example9@groundx.xyz", "992085223830").reject("", processor)
    );

    @WithAdminUser
    @DisplayName("파트너 가입 요청 목록 조회: 요청 상태 > 200")
    @Test
    void getPartnerApplications_APPLIED(@Autowired MockMvc mvc) throws Exception {
        // given
        partnerApplicationRepository.saveAll(applications);
        partnerApplicationRepository.flush();

        // when, then
        mvc.perform(get("/admin/v1/partner-applications?status={0}", APPLIED.toDisplay())
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(3L))
           .andExpect(jsonPath("$[0].businessName").value("(주) 그라운드엑스2"))
           .andExpect(jsonPath("$[0].processedAt").isEmpty())

           .andExpect(jsonPath("$[1].businessName").value("(주) 그라운드엑스1"))
           .andExpect(jsonPath("$[1].processedAt").isEmpty())

           .andExpect(jsonPath("$[2].businessName").value("(주) 그라운드엑스0"))
           .andExpect(jsonPath("$[2].processedAt").isEmpty());
    }

    @WithAdminUser
    @DisplayName("파트너 가입 요청 목록 조회: 거절 상태 > 200")
    @Test
    void getPartnerApplications_REJECTED(@Autowired MockMvc mvc) throws Exception {
        // given
        partnerApplicationRepository.saveAll(applications);
        partnerApplicationRepository.flush();

        // when, then
        mvc.perform(get("/admin/v1/partner-applications?status={0}", REJECTED.toDisplay())
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(3L))
           .andExpect(jsonPath("$[0].businessName").value("(주) 그라운드엑스8"))
           .andExpect(jsonPath("$[0].processedAt").isNotEmpty())
           //           .andExpect(jsonPath("$[0].processorNickname").value("jordan.jung"))  // TODO change to admin nickname (current : applicant's nickname)
           .andExpect(jsonPath("$[1].businessName").value("(주) 그라운드엑스7"))
           .andExpect(jsonPath("$[2].businessName").value("(주) 그라운드엑스6"));
    }

    @WithAdminUser
    @DisplayName("파트너 가입 요청 목록 조회: 비정상 입력 > 400")
    @Test
    void getPartnerApplications_UNDEFINED(@Autowired MockMvc mvc) throws Exception {
        // given
        partnerApplicationRepository.saveAll(applications);
        partnerApplicationRepository.flush();

        // when, then
        mvc.perform(get("/admin/v1/partner-applications?status={0}", "UNDEFINED")
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }
}

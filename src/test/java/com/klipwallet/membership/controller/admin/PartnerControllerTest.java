package com.klipwallet.membership.controller.admin;

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
import com.klipwallet.membership.repository.AdminRepository;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static com.klipwallet.membership.exception.ErrorCode.PARTNER_NOT_FOUND;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PartnerControllerTest {
    @Autowired
    PartnerApplicationRepository partnerApplicationRepository;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    AdminRepository adminRepository;

    @AfterEach
    void afterEach() {
        partnerRepository.deleteAll();
        partnerRepository.flush();
        partnerApplicationRepository.deleteAll();
        partnerApplicationRepository.flush();
    }

    MemberId creator = new MemberId(2);

    private void createPartner(String identifier, Integer klipDropsPartnerId) {
        PartnerApplication partnerApplication =
                new PartnerApplication("회사이름", "010-1234-5678", "000-00-0000" + klipDropsPartnerId, identifier + "@groundx.xyz",
                                       "oauth" + identifier);
        partnerApplication.setKlipDropsInfo(klipDropsPartnerId, identifier);
        partnerApplication.approve(creator);
        PartnerApplication partnerApplicationSaved = partnerApplicationRepository.save(partnerApplication);

        Partner partner = new Partner(partnerApplicationSaved.getId(), klipDropsPartnerId, partnerApplicationSaved.getBusinessName(),
                                      partnerApplicationSaved.getPhoneNumber(), partnerApplicationSaved.getBusinessRegistrationNumber(),
                                      partnerApplicationSaved.getEmail(), partnerApplicationSaved.getOauthId(), creator);
        partnerRepository.save(partner);

        partnerApplicationRepository.flush();
        partnerRepository.flush();
    }

    @WithAdminUser(memberId = 2)
    @DisplayName("가입한 파트너 목록 조회: 조회 성공 > 200")
    @Test
    void getPartnerApplicationDetail(@Autowired MockMvc mvc) throws Exception {
        // given
        String id = "gx1";
        createPartner(id, 100);

        // when, then
        Partner partner = partnerRepository.findByEmail(id + "@groundx.xyz")
                                           .orElseThrow();
        mvc.perform(get("/admin/v1/partners/{0}", partner.getId()).
                            contentType(APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(partner.getId()))
           .andExpect(jsonPath("$.businessName").value(partner.getName()))
           .andExpect(jsonPath("$.businessRegistrationNumber").value(partner.getBusinessRegistrationNumber()))
           .andExpect(jsonPath("$.email").value(partner.getEmail()))
           .andExpect(jsonPath("$.appliedAt").isNotEmpty())

           .andExpect(jsonPath("$.klipDropsPartnerId").value(partner.getKlipDropsPartnerId()))

           .andExpect(jsonPath("$.approveDetail").isNotEmpty())
           .andExpect(jsonPath("$.approveDetail.approvedBy").isNotEmpty())
           .andExpect(jsonPath("$.approveDetail.approvedBy.id").value(creator.value()))
           .andExpect(jsonPath("$.approveDetail.approvedBy.name").isNotEmpty())
           .andExpect(jsonPath("$.approveDetail.approvedAt").isNotEmpty());
    }

    @WithAdminUser(memberId = 2)
    @DisplayName("가입한 파트너 목록 조회: 존재하지 않는 파트너 > 404")
    @Test
    void getPartnerDetail(@Autowired MockMvc mvc) throws Exception {
        Integer partnerIdNotExist = 99999999;
        mvc.perform(get("/admin/v1/partners/{0}", partnerIdNotExist).
                            contentType(APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(PARTNER_NOT_FOUND.getCode()))
           .andExpect(jsonPath("$.err", startsWith("파트너를 찾을 수 없습니다. ID: %d".formatted(partnerIdNotExist))));
    }
}

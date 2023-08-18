package com.klipwallet.membership.service;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.dto.member.PartnerDto.ApproveRequest;
import com.klipwallet.membership.dto.member.PartnerDto.ApproveResult;
import com.klipwallet.membership.dto.member.PartnerDto.AcceptedPartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.AppliedPartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.RejectResult;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.exception.member.PartnerApplicationAlreadyProcessedException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.repository.AppliedPartnerRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@SpringBootTest
@Testcontainers
public class PartnerServiceTest {
    @Autowired
    PartnerService service;
    @Autowired
    AppliedPartnerRepository appliedPartnerRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @AfterEach
    void afterEach() {
        appliedPartnerRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @Test
    void getAppliedPartners() {
        // given
        List<String> names = Arrays.asList("(주) 그라운드엑스", "회사이름 (주)", "Winnie Corp.");

        AppliedPartner apply1 = new AppliedPartner(names.get(0), "010-1234-5678", "000-00-00001", "example1@groundx.xyz",
                                                   "192085223830.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply1);

        AppliedPartner apply2 = new AppliedPartner(names.get(1), "010-1234-5678", "000-00-00002", "example2@groundx.xyz",
                                                   "292085223830.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply2);
        AppliedPartner apply3 = new AppliedPartner(names.get(2), "010-1234-5678", "000-00-00003", "example3@groundx.xyz",
                                                   "392085223830.apps.googleusercontent.com");
        appliedPartnerRepository.save(apply3);

        // when
        List<AppliedPartnerDto> partners = service.getAppliedPartners();

        // then
        for (int i = 0; i < partners.size(); i++) {
            AppliedPartnerDto p = partners.get(i);
            assertThat(p.name()).isEqualTo(names.get(i));
        }
    }

    @Test
    void approvePartner_approve() throws Exception {
        // given
        com.klipwallet.membership.dto.member.PartnerDto.Apply apply =
                new com.klipwallet.membership.dto.member.PartnerDto.Apply("(주) 그라운드엑스", "010-1234-5678", "000-00-00004", "example4@groundx.xyz",
                                                                          "492085223830.apps.googleusercontent.com");
        service.apply(apply);

        // when
        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00004").getId();
        ApproveRequest request = new ApproveRequest(id);
        ApproveResult result = service.approvePartner(request);

        // then
        assertThat(result.name()).isEqualTo("(주) 그라운드엑스");

        AppliedPartner appliedPartner = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00004");
        assertThat(appliedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(appliedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(appliedPartner.getEmail()).isEqualTo("example4@groundx.xyz");
        assertThat(appliedPartner.getOAuthId()).isEqualTo("492085223830.apps.googleusercontent.com");
        assertThat(appliedPartner.getStatus()).isEqualTo(Status.APPROVED);

        Partner partner = partnerRepository.findByBusinessRegistrationNumber("000-00-00004");
        assertThat(partner).isNotNull();
        assertThat(partner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(partner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(partner.getEmail()).isEqualTo("example4@groundx.xyz");
        assertThat(partner.getOAuthId()).isEqualTo("492085223830.apps.googleusercontent.com");
    }

    @Test
    void approvePartner_reject() throws Exception {
        // given
        com.klipwallet.membership.dto.member.PartnerDto.Apply apply =
                new com.klipwallet.membership.dto.member.PartnerDto.Apply("(주) 그라운드엑스", "010-1234-5678", "000-00-00005", "example5@groundx.xyz",
                                                                          "592085223830.apps.googleusercontent.com");
        service.apply(apply);

        // when
        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00005").getId();
        com.klipwallet.membership.dto.member.PartnerDto.RejectRequest request =
                new com.klipwallet.membership.dto.member.PartnerDto.RejectRequest(id, "정상적이지 않은 사업자번호입니다.");
        RejectResult result = service.rejectPartner(request);

        // then
        assertThat(result.name()).isEqualTo("(주) 그라운드엑스");

        AppliedPartner appliedPartner = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00005");
        assertThat(appliedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(appliedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(appliedPartner.getEmail()).isEqualTo("example5@groundx.xyz");
        assertThat(appliedPartner.getOAuthId()).isEqualTo("592085223830.apps.googleusercontent.com");
        assertThat(appliedPartner.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(appliedPartner.getRejectReason()).isEqualTo("정상적이지 않은 사업자번호입니다.");

        Partner partner = partnerRepository.findByBusinessRegistrationNumber("000-00-00005");
        assertThat(partner).isNull();
    }

    @Test
    void approvePartner_throwNotFoundException() {
        // when
        ApproveRequest request = new ApproveRequest(1);

        Throwable thrown = catchThrowable(() -> {service.approvePartner(request);});

        // then
        assertThat(thrown).isInstanceOf(PartnerNotFoundException.class);

        List<AppliedPartnerDto> appliedPartners = service.getAppliedPartners();
        assertThat(appliedPartners.size()).isEqualTo(0);
    }

    @Test
    void approvePartner_throwAlreadyProcessedException() throws Exception {
        // given
        com.klipwallet.membership.dto.member.PartnerDto.Apply apply =
                new com.klipwallet.membership.dto.member.PartnerDto.Apply("(주) 그라운드엑스", "010-1234-5678", "000-00-00006", "example6@groundx.xyz",
                                                                          "692085223830.apps.googleusercontent.com");
        service.apply(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00006").getId();
        ApproveRequest request = new ApproveRequest(id);
        ApproveResult result = service.approvePartner(request);
        assertThat(result.name()).isEqualTo("(주) 그라운드엑스");

        // when
        Throwable thrown = catchThrowable(() -> {service.approvePartner(request);});

        // then
        assertThat(thrown).isInstanceOf(PartnerApplicationAlreadyProcessedException.class);
    }

    @Test
    void getApprovedPartners() {
        // given
        List<String> names = Arrays.asList("(주) 그라운드엑스", "회사이름 (주)", "Winnie Corp.");

        Partner approved1 = new Partner(names.get(0), "010-1234-5678", "000-00-00001", "example1@groundx.xyz",
                                        "192085223830.apps.googleusercontent.com");
        partnerRepository.save(approved1);

        Partner approved2 = new Partner(names.get(1), "010-1234-5678", "000-00-00002", "example2@groundx.xyz",
                                        "292085223830.apps.googleusercontent.com");
        partnerRepository.save(approved2);
        Partner approved3 = new Partner(names.get(2), "010-1234-5678", "000-00-00003", "example3@groundx.xyz",
                                        "392085223830.apps.googleusercontent.com");
        partnerRepository.save(approved3);

        // when
        List<AcceptedPartnerDto> partners = service.getApprovedPartners();

        // then
        for (int i = 0; i < partners.size(); i++) {
            AcceptedPartnerDto p = partners.get(i);
            assertThat(p.name()).isEqualTo(names.get(i));
        }
    }
}

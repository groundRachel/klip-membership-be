package com.klipwallet.membership.service;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.dto.member.PartnerDto;
import com.klipwallet.membership.entity.AcceptedPartner;
import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.exception.member.PartnerApplicationAlreadyProcessedException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.AcceptedPartnerRepository;
import com.klipwallet.membership.repository.AppliedPartnerRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public class PartnerServiceTest {
    @Autowired
    PartnerService service;
    @Autowired
    AppliedPartnerRepository appliedPartnerRepository;
    @Autowired
    AcceptedPartnerRepository acceptedPartnerRepository;

    @AfterEach
    void afterEach() {
        appliedPartnerRepository.deleteAll();
        acceptedPartnerRepository.deleteAll();
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
        List<PartnerDto.AppliedPartnersResult> partners = service.getAppliedPartners();

        // then
        for (int i = 0; i < partners.size(); i++) {
            PartnerDto.AppliedPartnersResult p = partners.get(i);
            assertThat(p.name()).isEqualTo(names.get(i));
        }
    }

    @Test
    void acceptPartner_accept() throws Exception {
        // given
        PartnerDto.Apply apply =
                new PartnerDto.Apply("(주) 그라운드엑스", "010-1234-5678", "000-00-00004", "example4@groundx.xyz",
                                     "492085223830.apps.googleusercontent.com");
        service.apply(apply);

        // when
        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00004").getId();
        PartnerDto.AcceptRequest request = new PartnerDto.AcceptRequest(id, Status.ACCEPTED, "");
        PartnerDto.AcceptResult result = service.acceptPartner(request);

        // then
        assertThat(result.name()).isEqualTo("(주) 그라운드엑스");

        AppliedPartner appliedPartner = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00004");
        assertThat(appliedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(appliedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(appliedPartner.getEmail()).isEqualTo("example4@groundx.xyz");
        assertThat(appliedPartner.getOAuthId()).isEqualTo("492085223830.apps.googleusercontent.com");
        assertThat(appliedPartner.getStatus()).isEqualTo(Status.ACCEPTED);

        AcceptedPartner acceptedPartner = acceptedPartnerRepository.findByBusinessRegistrationNumber("000-00-00004");
        assertThat(acceptedPartner).isNotNull();
        assertThat(acceptedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(acceptedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(acceptedPartner.getEmail()).isEqualTo("example4@groundx.xyz");
        assertThat(acceptedPartner.getOAuthId()).isEqualTo("492085223830.apps.googleusercontent.com");
    }

    @Test
    void acceptPartner_decline() throws Exception {
        // given
        PartnerDto.Apply apply =
                new PartnerDto.Apply("(주) 그라운드엑스", "010-1234-5678", "000-00-00005", "example5@groundx.xyz",
                                     "592085223830.apps.googleusercontent.com");
        service.apply(apply);

        // when
        PartnerDto.AcceptRequest request = new PartnerDto.AcceptRequest(1, Status.DECLINED, "정상적이지 않은 사업자번호입니다.");
        PartnerDto.AcceptResult result = service.acceptPartner(request);

        // then
        assertThat(result.name()).isEqualTo("(주) 그라운드엑스");

        AppliedPartner appliedPartner = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00005");
        assertThat(appliedPartner.getName()).isEqualTo("(주) 그라운드엑스");
        assertThat(appliedPartner.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(appliedPartner.getEmail()).isEqualTo("example5@groundx.xyz");
        assertThat(appliedPartner.getOAuthId()).isEqualTo("592085223830.apps.googleusercontent.com");
        assertThat(appliedPartner.getStatus()).isEqualTo(Status.DECLINED);
        assertThat(appliedPartner.getDeclineReason()).isEqualTo("정상적이지 않은 사업자번호입니다.");

        AcceptedPartner acceptedPartner = acceptedPartnerRepository.findByBusinessRegistrationNumber("000-00-00005");
        assertThat(acceptedPartner).isNull();
    }

    @Test
    void acceptPartner_throwNotFoundException() {
        // when
        PartnerDto.AcceptRequest request = new PartnerDto.AcceptRequest(1, Status.ACCEPTED, "");

        Throwable thrown = catchThrowable(() -> {service.acceptPartner(request);});

        // then
        assertThat(thrown).isInstanceOf(PartnerNotFoundException.class);

        List<PartnerDto.AppliedPartnersResult> appliedPartners = service.getAppliedPartners();
        assertThat(appliedPartners.size()).isEqualTo(0);
    }

    @Test
    void acceptPartner_throwAlreadyProcessedException() throws Exception {
        // given
        PartnerDto.Apply apply =
                new PartnerDto.Apply("(주) 그라운드엑스", "010-1234-5678", "000-00-00006", "example6@groundx.xyz",
                                     "692085223830.apps.googleusercontent.com");
        service.apply(apply);

        Integer id = appliedPartnerRepository.findByBusinessRegistrationNumber("000-00-00006").getId();
        PartnerDto.AcceptRequest request = new PartnerDto.AcceptRequest(id, Status.ACCEPTED, "");
        PartnerDto.AcceptResult result = service.acceptPartner(request);
        assertThat(result.name()).isEqualTo("(주) 그라운드엑스");

        // when
        Throwable thrown = catchThrowable(() -> {service.acceptPartner(request);});

        // then
        assertThat(thrown).isInstanceOf(PartnerApplicationAlreadyProcessedException.class);
    }

    @Test
    void getAcceptedPartners() {
        // given
        List<String> names = Arrays.asList("(주) 그라운드엑스", "회사이름 (주)", "Winnie Corp.");

        AcceptedPartner accepted1 = new AcceptedPartner(names.get(0), "010-1234-5678", "000-00-00001", "example1@groundx.xyz",
                                                        "192085223830.apps.googleusercontent.com");
        acceptedPartnerRepository.save(accepted1);

        AcceptedPartner accepted2 = new AcceptedPartner(names.get(1), "010-1234-5678", "000-00-00002", "example2@groundx.xyz",
                                                        "292085223830.apps.googleusercontent.com");
        acceptedPartnerRepository.save(accepted2);
        AcceptedPartner accepted3 = new AcceptedPartner(names.get(2), "010-1234-5678", "000-00-00003", "example3@groundx.xyz",
                                                        "392085223830.apps.googleusercontent.com");
        acceptedPartnerRepository.save(accepted3);

        // when
        List<PartnerDto.AcceptedPartnersResult> partners = service.getAcceptedPartners();

        // then
        for (int i = 0; i < partners.size(); i++) {
            PartnerDto.AcceptedPartnersResult p = partners.get(i);
            assertThat(p.name()).isEqualTo(names.get(i));
        }
    }
}

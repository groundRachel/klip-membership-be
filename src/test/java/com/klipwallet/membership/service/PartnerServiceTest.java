package com.klipwallet.membership.service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.repository.AdminRepository;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PartnerServiceTest {
    @Autowired
    PartnerService service;

    @Autowired
    PartnerApplicationRepository partnerApplicationRepository;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    private AdminRepository adminRepository;

    @AfterEach
    void afterEach() {
        partnerRepository.deleteAll();
        partnerRepository.flush();
        partnerApplicationRepository.deleteAll();
        partnerApplicationRepository.flush();
    }

    private record partnerInfo(
            String name,
            String phoneNumber,
            String businessRegistrationNumber,
            String email,
            String oauthId
    ) {}

    private MemberId createAdmin() {
        Admin admin = new Admin("jordan.jung@groundx.xyz", new MemberId(1));
        Admin persisted = adminRepository.save(admin);
        adminRepository.flush();
        return persisted.getMemberId();
    }


    @NotNull
    private List<partnerInfo> createPartnerInfos(MemberId processorId) {
        List<partnerInfo> partnerInfos = Arrays.asList(
                new partnerInfo("(주) 그라운드엑스", "010-1234-5678", "000-00-00001", "example1@groundx.xyz", "192085223830.apps.googleusercontent.com"),
                new partnerInfo("회사이름 (주)", "010-1234-5678", "00-00002", "example2@groundx.xyz", "292085223830.apps.googleusercontent.com"),
                new partnerInfo("Winnie Corp.", "010-1234-5678", "000-00-00003", "example3@groundx.xyz", "392085223830.apps.googleusercontent.com")
        );

        for (partnerInfo p : partnerInfos) {
            PartnerApplication partnerApplication = new PartnerApplication(p.name, p.phoneNumber, p.businessRegistrationNumber, p.email, p.oauthId);
            partnerApplication.approve(processorId);
            partnerApplicationRepository.save(partnerApplication);
        }
        partnerApplicationRepository.flush();
        partnerRepository.flush();
        return partnerInfos;
    }

    @WithAdminUser(memberId = 1)
    @Test
    void getApprovedPartners() {
        // given
        MemberId processorId = createAdmin();
        List<partnerInfo> partnerInfos = createPartnerInfos(processorId);

        // when
        List<ApprovedPartnerDto> partners = service.getPartners(PageRequest.of(0, 20));

        // then
        assertThat(partners.size()).isEqualTo(partnerInfos.size());
        for (int i = 0; i < partners.size(); i++) {
            ApprovedPartnerDto actual = partners.get(i);
            partnerInfo expected = partnerInfos.get(partners.size() - i - 1);

            assertThat(actual.id()).isNotNull();
            assertThat(actual.name()).isEqualTo(expected.name());
            assertThat(actual.processedAt()).isBefore(OffsetDateTime.now());
            assertThat(actual.processor().name()).isEqualTo("jordan.jung");
        }
    }

}

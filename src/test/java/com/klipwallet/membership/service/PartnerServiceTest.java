package com.klipwallet.membership.service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.dto.partner.PartnerDto.Detail;
import com.klipwallet.membership.dto.partner.PartnerDto.Update;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
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
        adminRepository.deleteAll();
        adminRepository.flush();
    }

    private record partnerInfo(
            String name,
            String phoneNumber,
            String businessRegistrationNumber,
            String email,
            String oauthId
    ) {}

    @NotNull
    private List<partnerInfo> createPartnerInfos(MemberId processorId) {
        List<partnerInfo> partnerInfos = Arrays.asList(
                new partnerInfo("(주) 그라운드엑스", "010-1234-5678", "000-00-00001", "example1@groundx.xyz", "192085223830.apps.googleusercontent.com"),
                new partnerInfo("회사이름 (주)", "010-1234-5678", "00-00002", "example2@groundx.xyz", "292085223830.apps.googleusercontent.com"),
                new partnerInfo("Winnie Corp.", "010-1234-5678", "000-00-00003", "example3@groundx.xyz", "392085223830.apps.googleusercontent.com")
        );

        for (int i = 0; i < partnerInfos.size(); i++) {
            partnerInfo p = partnerInfos.get(i);
            PartnerApplication partnerApplication = new PartnerApplication(p.name, p.phoneNumber, p.businessRegistrationNumber, p.email, p.oauthId);
            partnerApplication.approve(processorId);
            PartnerApplication savedApplication = partnerApplicationRepository.save(partnerApplication);
            partnerRepository.save(
                    new Partner(savedApplication.getId(), i, p.name, p.phoneNumber, p.businessRegistrationNumber, p.email, p.oauthId, processorId));
        }
        partnerApplicationRepository.flush();
        partnerRepository.flush();
        return partnerInfos;
    }

    @WithAdminUser(memberId = 1)
    @Test
    void getPartners() {
        // given
        MemberId processorId = new MemberId(2);
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

    @Test
    void getDetail() {
        // given
        MemberId processorId = new MemberId(2);
        List<partnerInfo> partnerInfos = createPartnerInfos(processorId);

        // when
        List<Detail> details = partnerInfos.stream()
                                           .map(info -> partnerRepository.findByEmail(info.email))
                                           .flatMap(Optional::stream)
                                           .map(partner -> service.getDetail(partner.getMemberId()))
                                           .toList();

        // then
        assertThat(details.size()).isEqualTo(partnerInfos.size());
        for (int i = 0; i < partnerInfos.size(); i++) {
            partnerInfo partnerInfo = partnerInfos.get(i);
            Detail detail = details.get(i);

            assertThat(detail.name()).isEqualTo(partnerInfo.name);
            assertThat(detail.businessRegistrationNumber()).isEqualTo(partnerInfo.businessRegistrationNumber);
            assertThat(detail.phoneNumber()).isEqualTo(partnerInfo.phoneNumber);
        }
    }

    @Test
    void update() {
        // given
        MemberId processorId = new MemberId(2);
        partnerInfo partnerInfo = createPartnerInfos(processorId).get(0);
        Partner partner = partnerRepository.findByEmail(partnerInfo.email).orElseThrow();

        String updateName = "(주) 변경된 이름";
        String updatePhoneNumber = "010-0000-0000";

        // when
        Detail updatedDetail = service.update(new Update(updateName, updatePhoneNumber), partner.getMemberId());

        // then
        assertThat(updatedDetail.name()).isEqualTo(updateName);
        assertThat(updatedDetail.businessRegistrationNumber()).isEqualTo(partnerInfo.businessRegistrationNumber);
        assertThat(updatedDetail.phoneNumber()).isEqualTo(updatePhoneNumber);

        Partner updatedPartner = partnerRepository.findByEmail(partnerInfo.email).orElseThrow();
        assertThat(updatedDetail.name()).isEqualTo(updatedPartner.getName());
        assertThat(updatedDetail.businessRegistrationNumber()).isEqualTo(updatedPartner.getBusinessRegistrationNumber());
        assertThat(updatedDetail.phoneNumber()).isEqualTo(updatedPartner.getPhoneNumber());
    }
}

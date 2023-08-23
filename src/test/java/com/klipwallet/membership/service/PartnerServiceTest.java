package com.klipwallet.membership.service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
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
            String oAuthId
    ) {}

    @Test
    void getApprovedPartners() {
        // given
        List<partnerInfo> partnerInfos = Arrays.asList(
                new partnerInfo("(주) 그라운드엑스", "010-1234-5678", "000-00-00001", "example1@groundx.xyz", "192085223830.apps.googleusercontent.com"),
                new partnerInfo("회사이름 (주)", "010-1234-5678", "00-00002", "example2@groundx.xyz", "292085223830.apps.googleusercontent.com"),
                new partnerInfo("Winnie Corp.", "010-1234-5678", "000-00-00003", "example3@groundx.xyz", "392085223830.apps.googleusercontent.com")
        );
        MemberId processor = new MemberId(23);

        for (partnerInfo p : partnerInfos) {
            partnerApplicationRepository.save(
                    new PartnerApplication(p.name, p.phoneNumber, p.businessRegistrationNumber, p.email, p.oAuthId).approve(processor));
            partnerRepository.save(new Partner(p.name, p.phoneNumber, p.businessRegistrationNumber, p.email, p.oAuthId, processor));
        }
        partnerApplicationRepository.flush();
        partnerRepository.flush();

        // when
        List<ApprovedPartnerDto> partners = service.getPartners(0, 20);

        // then
        assertThat(partners.size()).isEqualTo(partnerInfos.size());
        for (int i = 0; i < partners.size(); i++) {
            ApprovedPartnerDto actual = partners.get(i);
            partnerInfo expected = partnerInfos.get(partners.size() - i - 1);

            assertThat(actual.name()).isEqualTo(expected.name());
            assertThat(actual.processedAt()).isBefore(OffsetDateTime.now());
            assertThat(actual.processorId()).isEqualTo(processor);
        }
    }
}

package com.klipwallet.membership.service;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.PartnerApplicationRow;
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
    PartnerRepository partnerRepository;

    @AfterEach
    void afterEach() {
        partnerRepository.deleteAll();
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
        List<ApprovedPartnerDto> partners = service.getPartners();

        // then
        for (int i = 0; i < partners.size(); i++) {
            ApprovedPartnerDto p = partners.get(i);
            assertThat(p.name()).isEqualTo(names.get(i));
        }
    }
}

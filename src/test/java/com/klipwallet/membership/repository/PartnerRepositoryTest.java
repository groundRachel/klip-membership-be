package com.klipwallet.membership.repository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest         // TODO @DataJpaTest
public class PartnerRepositoryTest {
    @Autowired
    PartnerApplicationRepository partnerApplicationRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @BeforeEach
    void beforeEach() {
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
        PartnerApplication partnerApplicationSaved = partnerApplicationRepository.save(partnerApplication);


        Partner partner = new Partner(partnerApplicationSaved.getId(), klipDropsPartnerId, partnerApplicationSaved.getBusinessName(),
                                      partnerApplicationSaved.getPhoneNumber(), partnerApplicationSaved.getBusinessRegistrationNumber(),
                                      partnerApplicationSaved.getEmail(), partnerApplicationSaved.getOauthId(), creator);
        partnerRepository.save(partner);

        partnerApplicationRepository.flush();
        partnerRepository.flush();
    }


    @Test
    void findAllKlipDropsIdWithNoData() {
        // given
        // when
        List<Integer> allKlipDropsId = partnerRepository.findAllKlipDropsIds();

        // then
        assertThat(allKlipDropsId).isEmpty();
    }

    @Test
    void findAllKlipDropsIdWithNullOnly() {
        // given
        createPartner("gx1", null);
        createPartner("gx2", null);
        createPartner("gx3", null);

        // when
        List<Integer> allKlipDropsId = partnerRepository.findAllKlipDropsIds();

        // then
        assertThat(allKlipDropsId).isEmpty();
    }

    @Test
    void findAllKlipDropsIds() {
        // given
        createPartner("gx1", 5);
        createPartner("gx2", 2);
        createPartner("gx3", 8);

        // when
        List<Integer> allKlipDropsId = partnerRepository.findAllKlipDropsIds();

        // then
        assertThat(allKlipDropsId.size()).isEqualTo(3);
        assertThat(allKlipDropsId.get(0)).isEqualTo(2);
        assertThat(allKlipDropsId.get(1)).isEqualTo(5);
        assertThat(allKlipDropsId.get(2)).isEqualTo(8);
    }
}

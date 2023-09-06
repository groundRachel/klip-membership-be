package com.klipwallet.membership.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.klipwallet.membership.dto.nft.NftDto.Summary;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class NftServiceTest {
    @Autowired
    NftService service;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    PartnerApplicationRepository partnerApplicationRepository;

    @AfterEach
    void afterEach() {
        partnerRepository.deleteAll();
        partnerRepository.flush();
        partnerApplicationRepository.deleteAll();
        partnerApplicationRepository.flush();
    }

    private Partner createPartner() {
        PartnerApplication partnerApplication = new PartnerApplication("(주) 그라운드엑스", "010-1234-5678", "356-88-00968", "groundx@groundx.xyz",
                                                                       "192085223830.apps.googleusercontent.com");
        PartnerApplication persistentPartnerApplication = partnerApplicationRepository.save(partnerApplication);

        Integer kliDropsPartnerId = 1;
        return partnerRepository.save(
                new Partner(persistentPartnerApplication.getId(), kliDropsPartnerId, "그라운드엑스", "010-1234-5678", "356-88-00968", "groundx@groundx.xyz",
                            "192085223830.apps.googleusercontent.com", new MemberId(2)));
    }

    @Disabled("kubectl config set-context --current --namespace=klip-drops-dev ;" +
              "kubectl port-forward svc/klip-drops 3100:3100")
    @Test
    void getNftList() {
        // given
        Partner partner = createPartner();

        // when
        List<Summary> nftList = service.getNftList(partner.getMemberId());

        // then
        assertThat(nftList.size()).isNotZero();
        assertThat(nftList.get(0).name()).isNotEmpty();
        assertThat(nftList.get(0).creatorName()).isNotNull();
        assertThat(nftList.get(0).dropId()).isNotZero();
        assertThat(nftList.get(0).totalSalesCount()).isPositive();
        assertThat(nftList.get(0).totalSalesCount()).isPositive();
    }
}

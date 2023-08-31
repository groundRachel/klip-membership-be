package com.klipwallet.membership.adaptor.klipdrops;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartners;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Disabled("kubectl config set-context --current --namespace=klip-drops-dev" +
          "kubectl port-forward svc/klip-drops 3100:3100")
class KlipDropsInternalApiClientTest {
    @Autowired
    KlipDropsInternalApiClient klipDropsInternalApiClient;

    @Test
    void getPartnerByBusinessNumber() {
        String partnerId = "123-1234-1230";
        KlipDropsPartner partner = klipDropsInternalApiClient.getPartnerByBusinessNumber(partnerId);
        assertThat(partner.businessRegistrationNumber()).isEqualTo(partnerId);
        assertThat(partner.partnerId()).isNotZero();
        assertThat(partner.phoneNumber()).isNotEmpty();
        assertThat(partner.status()).isNotNull();
        assertThat(partner.createdAt()).isBefore(OffsetDateTime.now());
        assertThat(partner.updatedAt()).isBefore(OffsetDateTime.now());
    }

    @Test
    void getAllPartners() {
        KlipDropsPartners partners = klipDropsInternalApiClient.getAllPartners(null, null, null);
        assertThat(partners.klipDropsPartners().size()).isNotZero();
        assertThat(partners.cursor()).isNotEqualTo("0");
    }

    @Test
    void getAllPartnersWithSearch() {
        String searchByNum = "123";
        KlipDropsPartners partnersByNum = klipDropsInternalApiClient.getAllPartners(searchByNum, null, null);
        for (KlipDropsPartner partner : partnersByNum.klipDropsPartners()) {
            assertThat(partner.businessRegistrationNumber()).contains(searchByNum);
        }

        String searchByName = "Winnie";
        KlipDropsPartners partnersByName = klipDropsInternalApiClient.getAllPartners(searchByName, null, null);
        for (KlipDropsPartner partner : partnersByName.klipDropsPartners()) {
            assertThat(partner.name()).contains(searchByName);
        }
    }

    @Test
    void getAllPartnersWithCursorAndSize() {
        Integer size = 3;
        KlipDropsPartners partnersFirst = klipDropsInternalApiClient.getAllPartners(null, null, size);
        assertThat(partnersFirst.klipDropsPartners().size()).isEqualTo(size);
        assertThat(partnersFirst.cursor()).isNotEqualTo("0");

        String cursor = partnersFirst.cursor();
        KlipDropsPartners partnersSecond = klipDropsInternalApiClient.getAllPartners(null, cursor, size);
        assertThat(partnersSecond.klipDropsPartners().size()).isEqualTo(size);

        List<KlipDropsPartner> partnersAll = Stream.concat(partnersFirst.klipDropsPartners().stream(),
                                                           partnersSecond.klipDropsPartners().stream()).toList();
        for (int i = 0; i < partnersAll.size() - 1; i++) {
            KlipDropsPartner now = partnersAll.get(i);
            KlipDropsPartner next = partnersAll.get(i + 1);

            assertThat(now.partnerId()).isLessThan(next.partnerId());
        }
    }

    @Test
    void getDropsByPartner() {
        // TODO KLDV-3068
    }

    @Test
    void getDropsByIds() {
        // TODO KLDV-3068
    }
}

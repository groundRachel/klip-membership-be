package com.klipwallet.membership.adaptor.klipdrops;

import java.time.LocalDateTime;
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
@Disabled("수동으로 한번만 테스트")
class KlipDropsApiClientTest {
    @Autowired
    KlipDropsApiClient klipDropsApiClient;

    @Test
    void getPartnerByBusinessNumber() {
        String partnerId = "123-1234-1230";
        KlipDropsPartner partner = klipDropsApiClient.getPartnerByBusinessNumber(partnerId);
        assertThat(partner.businessRegistrationNumber()).isEqualTo(partnerId);
        assertThat(partner.partnerId()).isNotZero();
        assertThat(partner.phoneNumber()).isNotEmpty();
        assertThat(partner.status()).isNotEmpty();
        assertThat(partner.createdAt()).isBefore(LocalDateTime.now());
        assertThat(partner.updatedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    void getAllPartners() {
        KlipDropsPartners partners = klipDropsApiClient.getAllPartners(null, null, null);
        assertThat(partners.klipDropsPartners().size()).isNotZero();
        assertThat(partners.cursor()).isNotEqualTo("0");
    }

    @Test
    void getAllPartnersWithSearch() {
        String searchByNum = "123";
        KlipDropsPartners partnersByNum = klipDropsApiClient.getAllPartners(searchByNum, null, null);
        for (KlipDropsPartner partner : partnersByNum.klipDropsPartners()) {
            assertThat(partner.businessRegistrationNumber()).contains(searchByNum);
        }

        String searchByName = "Winnie";
        KlipDropsPartners partnersByName = klipDropsApiClient.getAllPartners(searchByName, null, null);
        for (KlipDropsPartner partner : partnersByName.klipDropsPartners()) {
            assertThat(partner.name()).contains(searchByName);
        }
    }

    @Test
    void getAllPartnersWithCursorAndSize() {
        Integer size = 3;
        KlipDropsPartners partnersFirst = klipDropsApiClient.getAllPartners(null, null, size);
        assertThat(partnersFirst.klipDropsPartners().size()).isEqualTo(size);
        assertThat(partnersFirst.cursor()).isNotEqualTo("0");

        String cursor = partnersFirst.cursor();
        KlipDropsPartners partnersSecond = klipDropsApiClient.getAllPartners(null, cursor, size);
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

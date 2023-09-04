package com.klipwallet.membership.adaptor.klipdrops;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.klipwallet.membership.adaptor.klipdrops.dto.Drop;
import com.klipwallet.membership.adaptor.klipdrops.dto.Drops;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartners;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Disabled("kubectl config set-context --current --namespace=klip-drops-dev ;" +
          "kubectl port-forward svc/klip-drops 3100:3100")
class KlipDropsInternalApiClientTest {
    @Autowired
    KlipDropsInternalApiClient klipDropsInternalApiClient;

    @Test
    void getAllPartnerWithBusinessNumber() {
        String businessRegistrationNumber = "123-1234-1230";
        KlipDropsPartners partners = klipDropsInternalApiClient.getAllPartners(businessRegistrationNumber, null, null, null);

        assertThat(partners.cursor()).isEmpty();
        assertThat(partners.klipDropsPartners().size()).isOne();

        KlipDropsPartner partner = partners.klipDropsPartners().get(0);
        assertThat(partner.businessRegistrationNumber()).isEqualTo(businessRegistrationNumber);
        assertThat(partner.partnerId()).isNotZero();
        assertThat(partner.phoneNumber()).isNotEmpty();
        assertThat(partner.status()).isNotNull();
        assertThat(partner.createdAt()).isBefore(OffsetDateTime.now());
        assertThat(partner.updatedAt()).isBefore(OffsetDateTime.now());
    }

    @Test
    void getAllPartners() {
        KlipDropsPartners partners = klipDropsInternalApiClient.getAllPartners(null, null, null, null);
        assertThat(partners.klipDropsPartners().size()).isNotZero();
        assertThat(partners.cursor()).isNotEqualTo("0");
    }

    @Test
    void getAllPartnersWithSearch() {
        String searchByNum = "123";
        KlipDropsPartners partnersByNum = klipDropsInternalApiClient.getAllPartners(null, searchByNum, null, null);
        for (KlipDropsPartner partner : partnersByNum.klipDropsPartners()) {
            assertThat(partner.businessRegistrationNumber()).contains(searchByNum);
        }

        String searchByName = "Winnie";
        KlipDropsPartners partnersByName = klipDropsInternalApiClient.getAllPartners(null, searchByName, null, null);
        for (KlipDropsPartner partner : partnersByName.klipDropsPartners()) {
            assertThat(partner.name()).contains(searchByName);
        }
    }

    @Test
    void getAllPartnersWithCursorAndSize() {
        Integer size = 3;
        KlipDropsPartners partnersFirst = klipDropsInternalApiClient.getAllPartners(null, null, null, size);
        assertThat(partnersFirst.klipDropsPartners().size()).isEqualTo(size);
        assertThat(partnersFirst.cursor()).isNotEqualTo("0");

        String cursor = partnersFirst.cursor();
        KlipDropsPartners partnersSecond = klipDropsInternalApiClient.getAllPartners(null, null, cursor, size);
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
        Integer size = 3;
        Integer partnerId = 43; // dev 환경에 drop 8 개 있는 계

        Drops dropsFirst = klipDropsInternalApiClient.getDropsByPartner(partnerId, 1, size);
        assertThat(dropsFirst.drops().size()).isEqualTo(size);
        assertThat(dropsFirst.totalCount()).isNotEqualTo(0);

        Drops dropsSecond = klipDropsInternalApiClient.getDropsByPartner(partnerId, 2, size);
        assertThat(dropsSecond.drops().size()).isEqualTo(size);
        assertThat(dropsSecond.totalCount()).isNotEqualTo(0);

        List<Drop> dropsAll = Stream.concat(dropsFirst.drops().stream(),
                                            dropsSecond.drops().stream()).toList();

        for (int i = 0; i < dropsAll.size() - 1; i++) {
            Drop now = dropsAll.get(i);
            Drop next = dropsAll.get(i + 1);

            assertThat(now.openAt()).isAfterOrEqualTo(next.openAt());
        }
    }

    @Test
    void getDropsByIds() {
        List<Integer> dropIds = List.of(3150048, 1010046);
        List<Drop> dropsByIds = klipDropsInternalApiClient.getDropsByIds(dropIds);

        assertThat(dropsByIds.size()).isEqualTo(dropIds.size());
    }
}

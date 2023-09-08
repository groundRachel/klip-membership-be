package com.klipwallet.membership.adaptor.klipdrops;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Disabled("kubectl config set-context --current --namespace=klip-drops-dev ;" +
          "kubectl port-forward svc/klip-drops 3100:3100")
class KlipDropsAdaptorTest {

    @Autowired
    KlipDropsAdaptor klipDropsAdaptor;

    @Test
    void getPartnerByIdNotExist() {
        Integer partnerIdNotExist = 88888888;
        KlipDropsPartner partnerById = klipDropsAdaptor.getPartnerById(partnerIdNotExist);
        assertThat(partnerById).isNull();
    }

    @Test
    void getPartnerByIdExist() {
        Integer partnerIdExist = 1;
        KlipDropsPartner partner = klipDropsAdaptor.getPartnerById(partnerIdExist);
        assertThat(partner).isNotNull();
        assertThat(partner.businessRegistrationNumber()).isNotEmpty();
        assertThat(partner.partnerId()).isNotZero();
        assertThat(partner.phoneNumber()).isNotEmpty();
        assertThat(partner.status()).isNotNull();
        assertThat(partner.createdAt()).isBefore(OffsetDateTime.now());
        assertThat(partner.updatedAt()).isBefore(OffsetDateTime.now());
    }
}
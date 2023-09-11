package com.klipwallet.membership.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.spockframework.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.dto.klipdrops.KlipDropsDto;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartnerStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Testcontainers
class KlipDropsPartnerServiceTest {
    @Autowired
    private KlipDropsPartnerService klipDropsPartnerService;
    @Autowired
    PartnerApplicationRepository partnerApplicationRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @MockBean
    KlipDropsService klipDropsService;

    @AfterEach
    void afterEach() {
        partnerRepository.deleteAll();
        partnerRepository.flush();
        partnerApplicationRepository.deleteAll();
        partnerApplicationRepository.flush();
    }

    private List<KlipDropsPartner> defaultKlipDropsPartners() {
        return Arrays.asList(new KlipDropsPartner("000-00-00000", 1, "그라운드엑스0", "010-1234-5678", ACTIVE, null, null),
                             new KlipDropsPartner("000-00-00001", 2, "그라운드엑스1", "010-1234-5678", ACTIVE, null, null),
                             new KlipDropsPartner("000-00-00003", 4, "그라운드엑스3", "010-1234-5678", ACTIVE, null, null),
                             new KlipDropsPartner("000-00-00004", 7, "그라운드엑스4", "010-1234-5678", ACTIVE, null, null),
                             new KlipDropsPartner("000-00-00005", 8, "그라운드엑스5", "010-1234-5678", ACTIVE, null, null)
        );
    }

    private void createPartner(Integer klipDropsPartnerId) {
        PartnerApplication partnerApplication =
                new PartnerApplication("회사이름", "010-1234-5678", "000-00-0000" + klipDropsPartnerId, "gx" + klipDropsPartnerId + "@groundx.xyz",
                                       "oauth" + klipDropsPartnerId);
        PartnerApplication partnerApplicationSaved = partnerApplicationRepository.save(partnerApplication);

        MemberId creator = new MemberId(2);
        Partner partner = new Partner(partnerApplicationSaved.getId(), klipDropsPartnerId, partnerApplicationSaved.getBusinessName(),
                                      partnerApplicationSaved.getPhoneNumber(), partnerApplicationSaved.getBusinessRegistrationNumber(),
                                      partnerApplicationSaved.getEmail(), partnerApplicationSaved.getOauthId(), creator);
        partnerRepository.save(partner);

        partnerApplicationRepository.flush();
        partnerRepository.flush();
    }

    @DisplayName("Klip Drops 파트너 목록 조회 : Klip Drops 파트너 목록이 없는 경우1 > Empty List")
    @Test
    void getDropsPartnersWhenNoApplicationData1() {
        // given
        given(klipDropsService.getAllPartners("")).willReturn(null);

        // when
        List<KlipDropsDto.Partner> klipDropsPartners = klipDropsPartnerService.getKlipDropsPartners("");

        // then
        assertThat(klipDropsPartners).isEmpty();
    }

    @DisplayName("Klip Drops 파트너 목록 조회 : Klip Drops 파트너 목록이 없는 경우2 > Empty List")
    @Test
    void getDropsPartnersWhenNoApplicationData2() {
        // given
        given(klipDropsService.getAllPartners("")).willReturn(new ArrayList<>());

        // when
        List<KlipDropsDto.Partner> klipDropsPartners = klipDropsPartnerService.getKlipDropsPartners("");

        // then
        assertThat(klipDropsPartners).isEmpty();
    }

    @DisplayName("Klip Drops 파트너 목록 조회 : Klip Drops 파트너가 모두 등록 된 경우 > Empty List")
    @Test
    void getDropsPartnersWhenAllRegistered() {
        // given
        given(klipDropsService.getAllPartners("")).willReturn(defaultKlipDropsPartners());
        for (KlipDropsPartner partner : defaultKlipDropsPartners()) {
            createPartner(partner.partnerId());
        }

        // when
        List<KlipDropsDto.Partner> klipDropsPartners = klipDropsPartnerService.getKlipDropsPartners("");

        // then
        assertThat(klipDropsPartners).isEmpty();
    }

    @DisplayName("Klip Drops 파트너 목록 조회 : Klip Drops 파트너가 있지만 Partner Application 데이터가 없는 경우")
    @Test
    void getDropsPartnersWhenNoApplicationData() {
        // given
        given(klipDropsService.getAllPartners("")).willReturn(defaultKlipDropsPartners());

        // when
        List<KlipDropsDto.Partner> klipDropsPartners = klipDropsPartnerService.getKlipDropsPartners("");

        // then
        List<Integer> expectedPartnerIds = new ArrayList<>(defaultKlipDropsPartners().stream().map(KlipDropsPartner::partnerId).toList());

        assertThat(klipDropsPartners.size()).isEqualTo(expectedPartnerIds.size());
        for (int i = 0; i < expectedPartnerIds.size(); i++) {
            KlipDropsDto.Partner actualPartner = klipDropsPartners.get(i);
            Integer expectPartnerId = expectedPartnerIds.get(i);

            assertThat(actualPartner.partnerId()).isEqualTo(expectPartnerId);
        }
    }

    static Stream<Pair<Integer[], Integer[]>> afterFiltered() {
        return Stream.of(
                // Klip Drops 파트너 목록과 Partner Application 데이터가 있는 경우
                Pair.of(new Integer[]{1, 2, 4, 7, 8}, new Integer[]{}), // 1 2 4 7 8
                Pair.of(new Integer[]{2, 7}, new Integer[]{1, 4, 8}),   // X 2 X 7 X
                Pair.of(new Integer[]{1}, new Integer[]{2, 4, 7, 8}),   // 1 X X X v
                Pair.of(new Integer[]{2}, new Integer[]{1, 4, 7, 8}),   // X 2 X X X
                Pair.of(new Integer[]{4}, new Integer[]{1, 2, 7, 8}),   // X X 4 X X
                Pair.of(new Integer[]{8}, new Integer[]{1, 2, 4, 7}),   // X X X X 8
                Pair.of(new Integer[]{1, 8}, new Integer[]{2, 4, 7}),   // 1 X X X 8
                Pair.of(new Integer[]{1, 2, 4}, new Integer[]{7, 8}),   // 1 2 4 X X
                Pair.of(new Integer[]{7, 8}, new Integer[]{1, 2, 4}),   // X X X 7 8
                Pair.of(new Integer[]{2, 4, 7}, new Integer[]{1, 8}),   // X 2 4 7 X
                Pair.of(new Integer[]{}, new Integer[]{1, 2, 4, 7, 8}), // X X X X X

                // Partner Application 데이터에 Klip Drops 파트너 목록에 없는 데이터가 있는 경우
                // (KMT 가입 후 KlipDrops Partners Tool은 탈퇴하는 경우)
                Pair.of(new Integer[]{0, 1, 2, 4, 7, 8}, new Integer[]{}),
                Pair.of(new Integer[]{0}, new Integer[]{1, 2, 4, 7, 8}),
                Pair.of(new Integer[]{-1, 1, 2, 4, 7, 8}, new Integer[]{}),
                Pair.of(new Integer[]{-1}, new Integer[]{1, 2, 4, 7, 8}),
                Pair.of(new Integer[]{-2, -1, 1, 2, 4, 7, 8}, new Integer[]{}),
                Pair.of(new Integer[]{-2, -1}, new Integer[]{1, 2, 4, 7, 8}),
                Pair.of(new Integer[]{-2, -1, 1}, new Integer[]{2, 4, 7, 8}),
                Pair.of(new Integer[]{1, 2, 4, 7, 8, 9}, new Integer[]{}),
                Pair.of(new Integer[]{9}, new Integer[]{1, 2, 4, 7, 8}),
                Pair.of(new Integer[]{1, 2, 4, 7, 8, 10}, new Integer[]{}),
                Pair.of(new Integer[]{10}, new Integer[]{1, 2, 4, 7, 8}),
                Pair.of(new Integer[]{1, 2, 4, 7, 8, 10, 11}, new Integer[]{}),
                Pair.of(new Integer[]{10, 11}, new Integer[]{1, 2, 4, 7, 8}),
                Pair.of(new Integer[]{7, 9}, new Integer[]{1, 2, 4, 8}),
                Pair.of(new Integer[]{7, 10}, new Integer[]{1, 2, 4, 8}),
                Pair.of(new Integer[]{8, 9}, new Integer[]{1, 2, 4, 7}),
                Pair.of(new Integer[]{8, 10}, new Integer[]{1, 2, 4, 7})
        );
    }

    @DisplayName("Klip Drops 파트너 목록 조회 : Klip Drops 파트너 목록과 Partner Application 데이터가 있는 경우")
    @ParameterizedTest
    @MethodSource("afterFiltered")
    void getDropsPartnersWhenApplicationData(Pair<Integer[], Integer[]> testcase) {
        Integer[] partnersInRepo = testcase.first();
        Integer[] expected = testcase.second();

        given(klipDropsService.getAllPartners("")).willReturn(defaultKlipDropsPartners());

        // given
        for (Integer partner : partnersInRepo) {
            createPartner(partner);
        }
        // when
        List<KlipDropsDto.Partner> klipDropsPartners = klipDropsPartnerService.getKlipDropsPartners("");

        // then
        assertThat(klipDropsPartners.size()).isEqualTo(expected.length);
        for (int i = 0; i < expected.length; i++) {
            KlipDropsDto.Partner actualPartner = klipDropsPartners.get(i);
            Integer expectPartnerId = expected[i];

            assertThat(actualPartner.partnerId()).isEqualTo(expectPartnerId);
        }

        partnerApplicationRepository.deleteAll();
        partnerApplicationRepository.flush();
        partnerRepository.deleteAll();
        partnerRepository.flush();

    }
}

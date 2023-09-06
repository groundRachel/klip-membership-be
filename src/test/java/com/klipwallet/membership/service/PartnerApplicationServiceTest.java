package com.klipwallet.membership.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.klipwallet.membership.config.security.KlipMembershipOAuth2User;
import com.klipwallet.membership.dto.partnerapplication.SignUpStatus;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PartnerApplicationServiceTest {
    @Autowired
    PartnerApplicationService partnerApplicationService;

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

    MemberId processorId = new MemberId(2);

    private PartnerApplication createApplication() {
        PartnerApplication partnerApplication = new PartnerApplication("(주) 그라운드엑스", "010-1234-5678", "000-00-00001", "example1@groundx.xyz",
                                                                       "192085223830.apps.googleusercontent.com");
        PartnerApplication persistent = partnerApplicationRepository.save(partnerApplication);
        partnerApplicationRepository.flush();

        return persistent;
    }

    @Test
    void getSignUpStatusOfSignedUp() {
        // given
        PartnerApplication partnerApplication = createApplication();

        partnerApplication.approve(processorId);
        partnerApplicationRepository.save(partnerApplication);
        partnerRepository.save(new Partner(partnerApplication.getId(), 3,
                                           partnerApplication.getBusinessName(), partnerApplication.getPhoneNumber(),
                                           partnerApplication.getBusinessRegistrationNumber(), partnerApplication.getEmail(),
                                           partnerApplication.getOauthId(), processorId));
        partnerApplicationRepository.flush();
        partnerRepository.flush();

        // when
        KlipMembershipOAuth2User klipMembershipOAuth2User = new KlipMembershipOAuth2User(null, emptyList(), "", partnerApplication.getEmail());
        SignUpStatus signUpStatus = partnerApplicationService.getSignUpStatus(klipMembershipOAuth2User);

        // then
        assertThat(signUpStatus).isEqualTo(SignUpStatus.SIGNED_UP);
    }

    @Test
    void getSignUpStatusOfPending() {
        // given
        PartnerApplication application = createApplication();

        // when
        KlipMembershipOAuth2User klipMembershipOAuth2User = new KlipMembershipOAuth2User(null, emptyList(), "", application.getEmail());
        SignUpStatus signUpStatus = partnerApplicationService.getSignUpStatus(klipMembershipOAuth2User);

        // then
        assertThat(signUpStatus).isEqualTo(SignUpStatus.PENDING);
    }

    @Test
    void getSignUpStatusOfUnapplied() {
        // given
        KlipMembershipOAuth2User klipMembershipOAuth2User = new KlipMembershipOAuth2User(null, emptyList(), "", "email@groundx.xyz");
        SignUpStatus signUpStatus = partnerApplicationService.getSignUpStatus(klipMembershipOAuth2User);

        // then
        assertThat(signUpStatus).isEqualTo(SignUpStatus.NON_MEMBER);
    }

    @Test
    void getSignUpStatusOfRejected() {
        // given
        PartnerApplication application = createApplication();

        application.reject("rejectReason", processorId);
        partnerApplicationRepository.save(application);
        partnerApplicationRepository.flush();
        partnerRepository.flush();

        // when
        KlipMembershipOAuth2User klipMembershipOAuth2User = new KlipMembershipOAuth2User(null, emptyList(), "", application.getEmail());
        SignUpStatus signUpStatus = partnerApplicationService.getSignUpStatus(klipMembershipOAuth2User);

        // then
        assertThat(signUpStatus).isEqualTo(SignUpStatus.NON_MEMBER);
    }
}
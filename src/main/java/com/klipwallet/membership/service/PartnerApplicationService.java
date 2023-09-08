package com.klipwallet.membership.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.dto.klipdrops.KlipDropsAssembler;
import com.klipwallet.membership.dto.klipdrops.KlipDropsDto;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationAssembler;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.Application;
import com.klipwallet.membership.dto.partnerapplication.PartnerApplicationDto.RejectRequest;
import com.klipwallet.membership.dto.partnerapplication.SignUpStatus;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.entity.PartnerApplicationCreated;
import com.klipwallet.membership.exception.klipdrops.KlipDropsParnterNotFoundByBusinessNumberException;
import com.klipwallet.membership.exception.klipdrops.KlipDropsParnterNotFoundByPartnerIdException;
import com.klipwallet.membership.exception.member.PartnerApplicationDuplicatedException;
import com.klipwallet.membership.exception.member.PartnerApplicationNotFoundException;
import com.klipwallet.membership.repository.PartnerApplicationRepository;
import com.klipwallet.membership.repository.PartnerRepository;

import static com.klipwallet.membership.entity.PartnerApplication.Status.APPLIED;
import static com.klipwallet.membership.entity.PartnerApplication.Status.APPROVED;


@Service
@RequiredArgsConstructor
public class PartnerApplicationService {
    private final PartnerApplicationRepository partnerApplicationRepository;
    private final PartnerApplicationAssembler partnerApplicationAssembler;
    private final PartnerRepository partnerRepository;
    private final KlipDropsService klipDropsService;
    private final KlipDropsAssembler klipDropsAssembler;

    private void verifyApply(AuthenticatedUser user) {
        if (partnerApplicationRepository.existsByEmailAndStatusIsIn(user.getEmail(), List.of(APPLIED, APPROVED))) {
            throw new PartnerApplicationDuplicatedException();
        }
    }

    @Transactional
    public PartnerApplicationDto.ApplyResult apply(Application body, AuthenticatedUser user) {
        verifyApply(user);

        PartnerApplication entity = body.toPartnerApplication(user);
        PartnerApplication partnerApplication = partnerApplicationRepository.save(entity);
        return partnerApplicationAssembler.toApplyResult(partnerApplication);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(value = PartnerApplicationCreated.class, phase = TransactionPhase.AFTER_COMMIT)
    public void getAndSetKlipDropsPartnerInfo(PartnerApplicationCreated event) {
        PartnerApplication partnerApplication = tryGetPartnerApplication(event.getPartnerApplicationId());

        String businessRegistrationNumber = partnerApplication.getBusinessRegistrationNumber();
        KlipDropsPartner partner = klipDropsService.getPartnerByBusinessRegistrationNumber(businessRegistrationNumber);
        if (partner == null) {
            throw new KlipDropsParnterNotFoundByBusinessNumberException(partnerApplication.getId(), businessRegistrationNumber);
        }
        partnerApplication.setKlipDropsInfo(partner.partnerId(), partner.name());
        partnerApplicationRepository.save(partnerApplication);
    }

    private PartnerApplication tryGetPartnerApplication(Integer applicationId) {
        return partnerApplicationRepository.findById(applicationId)
                                           .orElseThrow(() -> new PartnerApplicationNotFoundException(applicationId));
    }

    @Transactional(readOnly = true)
    public List<PartnerApplicationDto.PartnerApplicationRow> getPartnerApplications(Pageable page, Status status) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), toSort(status));

        Page<PartnerApplication> partnerApplications = partnerApplicationRepository.findAllByStatus(status, pageable);
        return partnerApplicationAssembler.toPartnerApplicationRow(partnerApplications);
    }

    @Transactional
    public PartnerApplicationDto.PartnerApplicationCount getPartnerApplicationNumber(Status status) {
        Long countByStatus = partnerApplicationRepository.countByStatus(status);
        return new PartnerApplicationDto.PartnerApplicationCount(countByStatus);
    }

    private Sort toSort(Status status) {
        if (status == APPLIED) {
            return Sort.sort(PartnerApplication.class).by(PartnerApplication::getCreatedAt).descending();
        }
        return Sort.sort(PartnerApplication.class).by(PartnerApplication::getProcessedAt).descending();
    }

    @Transactional
    public void approve(Integer applicationId, AuthenticatedUser user) {
        PartnerApplication partnerApplication = tryGetPartnerApplication(applicationId);

        boolean canSkipDuplicatedRequest = partnerApplication.approve(user.getMemberId());
        if (canSkipDuplicatedRequest) {
            return;
        }

        partnerApplicationRepository.save(partnerApplication);
        partnerRepository.save(new Partner(partnerApplication.getId(), partnerApplication.getKlipDropsPartnerId(),
                                           partnerApplication.getBusinessName(), partnerApplication.getPhoneNumber(),
                                           partnerApplication.getBusinessRegistrationNumber(), partnerApplication.getEmail(),
                                           partnerApplication.getOauthId(), user.getMemberId()));
    }

    @Transactional
    public void reject(Integer applicationId, RejectRequest body, AuthenticatedUser user) {
        PartnerApplication partnerApplication = tryGetPartnerApplication(applicationId);

        boolean canSkipDuplicatedRequest = partnerApplication.reject(body.rejectReason(), user.getMemberId());
        if (canSkipDuplicatedRequest) {
            return;
        }

        partnerApplicationRepository.save(partnerApplication);
    }

    @Transactional
    public PartnerApplicationDto.SignUpStatusResult getSignUpStatus(AuthenticatedUser user) {
        if (partnerRepository.existsByEmail(user.getEmail())) {
            return new PartnerApplicationDto.SignUpStatusResult(SignUpStatus.SIGNED_UP);
        }

        if (partnerApplicationRepository.existsByEmailAndStatusIsIn(user.getEmail(), List.of(APPLIED))) {
            return new PartnerApplicationDto.SignUpStatusResult(SignUpStatus.PENDING);
        }

        return new PartnerApplicationDto.SignUpStatusResult(SignUpStatus.NON_MEMBER);
    }

    @Transactional
    public void updateKlipDropsPartnerId(Integer partnerApplicationId, Integer klipDropsPartnerId) {
        PartnerApplication partnerApplication = tryGetPartnerApplication(partnerApplicationId);
        KlipDropsPartner klipDropsPartner = klipDropsService.getPartnerById(klipDropsPartnerId);

        verifyKlipDropsPartnerUpdatable(klipDropsPartnerId, klipDropsPartner);

        partnerApplication.setKlipDropsInfo(klipDropsPartner.partnerId(), klipDropsPartner.name());
        partnerApplicationRepository.save(partnerApplication);
    }

    private void verifyKlipDropsPartnerUpdatable(Integer klipDropsPartnerId, KlipDropsPartner klipDropsPartner) {
        if (klipDropsPartner == null) {
            throw new KlipDropsParnterNotFoundByPartnerIdException(klipDropsPartnerId);
        }
    }

    @Transactional(readOnly = true)
    public List<KlipDropsDto.Partner> getKlipDropsPartners(String search) {
        List<KlipDropsPartner> klipDropsPartners = klipDropsService.getAllPartners(search);
        if (klipDropsPartners == null || klipDropsPartners.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> klipDropsIdsFromRepo = partnerRepository.findAllKlipDropsIds();
        if (klipDropsIdsFromRepo == null || klipDropsIdsFromRepo.isEmpty()) {
            return klipDropsAssembler.toPartners(klipDropsPartners);
        }

        List<KlipDropsPartner> unusedPartners = filterUnusedPartners(klipDropsPartners, klipDropsIdsFromRepo);
        return klipDropsAssembler.toPartners(unusedPartners);
    }

    private List<KlipDropsPartner> filterUnusedPartners(List<KlipDropsPartner> klipDropsPartners, List<Integer> klipDropsIdsFromRepo) {
        List<KlipDropsPartner> unusedPartners = new LinkedList<>();

        int repoIndex = 0;
        Integer currentRepoId = getId(klipDropsIdsFromRepo, repoIndex);

        for (KlipDropsPartner klipDropsPartner : klipDropsPartners) {
            Integer fetchedId = klipDropsPartner.partnerId();

            while (fetchedId > currentRepoId) {
                repoIndex++;
                currentRepoId = getId(klipDropsIdsFromRepo, repoIndex);
            }

            if (fetchedId.equals(currentRepoId)) {
                repoIndex++;
                currentRepoId = getId(klipDropsIdsFromRepo, repoIndex);
            } else {
                unusedPartners.add(klipDropsPartner);
            }
        }
        return unusedPartners;
    }

    private Integer getId(List<Integer> klipDropsIdsFromRepo, int repoIndex) {
        if (repoIndex < klipDropsIdsFromRepo.size()) {
            return klipDropsIdsFromRepo.get(repoIndex);
        }
        return Integer.MAX_VALUE;
    }
}

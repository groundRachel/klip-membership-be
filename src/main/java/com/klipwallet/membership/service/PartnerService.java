package com.klipwallet.membership.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import feign.FeignException.BadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.member.PartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.AppliedPartnersResult;
import com.klipwallet.membership.entity.AcceptedPartner;
import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.exception.member.PartnerApplicationAlreadyProcessedException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.AcceptedPartnerRepository;
import com.klipwallet.membership.repository.AppliedPartnerRepository;

import static com.klipwallet.membership.entity.AppliedPartner.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerService {
    private final AppliedPartnerRepository appliedPartnerRepository;
    private final AcceptedPartnerRepository acceptedPartnerRepository;

    @Transactional
    public PartnerDto.ApplyResult apply(PartnerDto.Apply body) {
        AppliedPartner entity = body.toAppliedPartner();
        AppliedPartner appliedPartner = appliedPartnerRepository.save(entity);
        return new PartnerDto.ApplyResult(appliedPartner.getId(), appliedPartner.getName(), appliedPartner.getCreatedAt(),
                                          appliedPartner.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public List<PartnerDto.AppliedPartnersResult> getAppliedPartners() {
        // TODO KLDV-3068 get and check partner business number from drops
        // TODO consider adding a cache; some results are from drops
        List<AppliedPartner> appliedPartners = appliedPartnerRepository.findAll();
        return appliedPartners.stream()
                              .map(p -> new AppliedPartnersResult(p.getId(), p.getName(), p.getCreatedAt(), p.getStatus(), p.getDeclineReason()))
                              .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartnerDto.AcceptedPartnersResult> getAcceptedPartners() {
        List<AcceptedPartner> acceptedPartners = acceptedPartnerRepository.findAll();
        return acceptedPartners.stream()
                               .map(p -> new PartnerDto.AcceptedPartnersResult(p.getId(), p.getName(), p.getCreatedAt()))
                               .collect(Collectors.toList());
    }

    @Transactional
    public PartnerDto.AcceptResult acceptPartner(PartnerDto.AcceptRequest body) throws Exception {
        AppliedPartner appliedPartner = appliedPartnerRepository.findById(body.id())
                                                                .orElseThrow(() -> new PartnerNotFoundException(body.id()));

        if (appliedPartner.getStatus() != APPLIED) {
            throw new PartnerApplicationAlreadyProcessedException(appliedPartner);
        }

        switch (body.accept()) {
            case APPLIED -> throw new Exception();
            case ACCEPTED -> {
                setAppliedPartnerStatus(appliedPartner, Status.ACCEPTED, "");
                AcceptedPartner acceptedPartner = appliedPartner.toAcceptedPartner();
                acceptedPartnerRepository.save(acceptedPartner);

                // TODO KLDV-3077 add API caller info in log
                log.info("[파트너 가입 승인] id:%d, by:%d".formatted(body.id(), 0));
            }
            case DECLINED -> {
                setAppliedPartnerStatus(appliedPartner, Status.DECLINED, body.declineReason());
                // TODO KLDV-3077 add API caller info in log
                log.info("[파트너 가입 거절] id:%d, reason:%s, by:%d".formatted(body.id(), body.declineReason(), 0));
            }
        }

        // TODO KLDV-3069 send result by email

        return new PartnerDto.AcceptResult(appliedPartner.getName());
    }

    public void setAppliedPartnerStatus(AppliedPartner appliedPartner, Status status, String declineReason) {
        appliedPartner.setStatus(status);
        appliedPartner.setDeclineReason(declineReason);
        appliedPartnerRepository.save(appliedPartner);
    }
}

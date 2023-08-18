package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.member.PartnerDto.ApproveRequest;
import com.klipwallet.membership.dto.member.PartnerDto.ApproveResult;
import com.klipwallet.membership.dto.member.PartnerDto.AcceptedPartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.AppliedPartnerDto;
import com.klipwallet.membership.dto.member.PartnerDto.RejectRequest;
import com.klipwallet.membership.dto.member.PartnerDto.RejectResult;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.AppliedPartner;
import com.klipwallet.membership.entity.AppliedPartner.Status;
import com.klipwallet.membership.exception.member.PartnerApplicationAlreadyProcessedException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.repository.AppliedPartnerRepository;

import static com.klipwallet.membership.entity.AppliedPartner.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerService {
    private final AppliedPartnerRepository appliedPartnerRepository;
    private final PartnerRepository partnerRepository;

    private final PartnerAssembler partnerAssembler;

    @Transactional
    public com.klipwallet.membership.dto.member.PartnerDto.ApplyResult apply(com.klipwallet.membership.dto.member.PartnerDto.Apply body) {
        AppliedPartner entity = body.toAppliedPartner();
        AppliedPartner appliedPartner = appliedPartnerRepository.save(entity);
        return partnerAssembler.toApplyResult(appliedPartner);
    }

    @Transactional(readOnly = true)
    public List<AppliedPartnerDto> getAppliedPartners() {
        // TODO KLDV-3066 Pagination
        // TODO KLDV-3068 get and check partner business number from drops
        // TODO consider adding a cache; some results are from drops
        List<AppliedPartner> appliedPartners = appliedPartnerRepository.findAll();
        return partnerAssembler.toAppliedPartnerDto(appliedPartners);
    }

    @Transactional(readOnly = true)
    public List<AcceptedPartnerDto> getApprovedPartners() {
        // TODO KLDV-3070 Pagination
        List<Partner> partners = partnerRepository.findAll();
        return partnerAssembler.toPartnerDto(partners);
    }

    @Transactional
    public ApproveResult approvePartner(ApproveRequest body) {
        AppliedPartner appliedPartner = appliedPartnerRepository.findById(body.id())
                                                                .orElseThrow(() -> new PartnerNotFoundException(body.id()));

        if (appliedPartner.getStatus() != APPLIED) {
            throw new PartnerApplicationAlreadyProcessedException(appliedPartner);
        }

        setAppliedPartnerStatus(appliedPartner, Status.APPROVED, "");
        Partner partner = appliedPartner.toApprovedPartner();
        partnerRepository.save(partner);

        // TODO KLDV-3077 add API caller info in log
        log.info("[파트너 가입 승인] id:%d, by:%d".formatted(body.id(), 0));

        // TODO KLDV-3069 send result by email

        return new ApproveResult(appliedPartner.getName());
    }

    @Transactional
    public RejectResult rejectPartner(RejectRequest body) throws Exception {
        AppliedPartner appliedPartner = appliedPartnerRepository.findById(body.id())
                                                                .orElseThrow(() -> new PartnerNotFoundException(body.id()));
        if (appliedPartner.getStatus() != APPLIED) {
            throw new PartnerApplicationAlreadyProcessedException(appliedPartner);
        }

        setAppliedPartnerStatus(appliedPartner, Status.REJECTED, body.rejectReason());

        // TODO KLDV-3077 add API caller info in log
        log.info("[파트너 가입 거절] id:%d, reason:%s, by:%d".formatted(body.id(), body.rejectReason(), 0));

        // TODO KLDV-3069 send result by email

        return new RejectResult(appliedPartner.getName());
    }

    public void setAppliedPartnerStatus(AppliedPartner appliedPartner, Status status, String rejectReason) {
        appliedPartner.setStatus(status);
        appliedPartner.setRejectReason(rejectReason);
        appliedPartnerRepository.save(appliedPartner);
    }
}

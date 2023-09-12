package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.partner.PartnerAssembler;
import com.klipwallet.membership.dto.partner.PartnerDto.ApprovedPartnerDto;
import com.klipwallet.membership.dto.partner.PartnerDto.DetailByAdmin;
import com.klipwallet.membership.dto.partner.PartnerDto.DetailByTool;
import com.klipwallet.membership.dto.partner.PartnerDto.Update;
import com.klipwallet.membership.entity.Member;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication.Status;
import com.klipwallet.membership.entity.PartnerDetailView;
import com.klipwallet.membership.entity.PartnerSummaryView;
import com.klipwallet.membership.exception.MemberNotFoundException;
import com.klipwallet.membership.exception.member.PartnerNotFoundException;
import com.klipwallet.membership.repository.PartnerRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final PartnerAssembler partnerAssembler;

    @Transactional(readOnly = true)
    public List<ApprovedPartnerDto> getPartners(Pageable page) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), getSort());

        Page<PartnerSummaryView> partners = partnerRepository.findAllPartners(Status.APPROVED, pageable);
        return partnerAssembler.toPartnerDto(partners);
    }

    private Sort getSort() {
        return Sort.sort(PartnerSummaryView.class).by(PartnerSummaryView::getProcessedAt).descending();
    }

    public DetailByAdmin getPartnerDetail(Integer partnerId) {
        PartnerDetailView detail = partnerRepository.findPartnerDetailById(partnerId)
                                                    .orElseThrow(() -> new PartnerNotFoundException(new MemberId(partnerId)));
        return partnerAssembler.toDetailByAdmin(detail);
    }

    /**
     * 인증 후 파트너 반환
     *
     * @param oauthId 인증한 OAuthID
     * @throws com.klipwallet.membership.exception.MemberNotFoundException OAuthID에 맞는 파트너가 없는 경우
     */
    @Transactional(readOnly = true)
    public Partner signIn(String oauthId) {
        return partnerRepository.findByOauthId(oauthId)
                                .filter(Member::isEnabled)
                                .orElseThrow(MemberNotFoundException::new);
    }

    private Partner tryGetPartner(MemberId partnerId) {
        return partnerRepository.findById(partnerId.value())
                                .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public DetailByTool getDetail(MemberId partnerId) {
        Partner partner = tryGetPartner(partnerId);
        return partnerAssembler.toDetailByTool(partner);
    }

    @Transactional
    public DetailByTool update(Update command, MemberId partnerId) {
        Partner partner = tryGetPartner(partnerId);

        partner.update(command.name(), command.phoneNumber());
        Partner persistent = partnerRepository.save(partner);

        return partnerAssembler.toDetailByTool(persistent);
    }
}

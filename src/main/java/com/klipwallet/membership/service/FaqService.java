package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.faq.FaqAssembler;
import com.klipwallet.membership.dto.faq.FaqCreate;
import com.klipwallet.membership.dto.faq.FaqDetail;
import com.klipwallet.membership.dto.faq.FaqStatus;
import com.klipwallet.membership.dto.faq.FaqSummary;
import com.klipwallet.membership.dto.faq.FaqUpdate;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Faq;
import com.klipwallet.membership.entity.FaqUpdatable;
import com.klipwallet.membership.exception.FaqNotFoundException;
import com.klipwallet.membership.repository.FaqRepository;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;
    private final FaqAssembler faqAssembler;


    /**
     * FAQ 생성
     *
     * @param command 생성할 FAQ 내용
     * @param user    생성자
     * @return 생성된 FAQ 요약
     */
    @Transactional
    public FaqSummary create(FaqCreate command, AuthenticatedUser user) {
        Faq entity = command.toFAQ(user);
        Faq saved = faqRepository.save(entity);
        return new FaqSummary(saved);
    }

    /**
     * FAQ 수정
     *
     * @param faqId 수정할 FAQ ID
     * @param command  수정할 FAQ 내용
     * @param user     수정자
     * @return 수정된 FAQ 상세
     */
    @Transactional
    public FaqDetail update(Integer faqId, FaqUpdate command, AuthenticatedUser user) {
        FaqUpdatable updatable = command.toUpdatable(user);
        Faq faq = tryGetNotice(faqId);

        faq.update(updatable);
        Faq saved = faqRepository.save(faq);

        return faqAssembler.toDetail(saved);
    }

    /**
     * FAQ 상태 변경
     *
     * @param faqId 수정할 FAQ ID
     * @param command  상태 변경 DTO
     * @param user     수정자
     * @return 변경된 상태 DTO
     */
    @Transactional
    public FaqStatus changeStatus(Integer faqId, FaqStatus command, AuthenticatedUser user) {
        Faq faq = tryGetNotice(faqId);
        faq.changeStatus(command.status(), user.getMemberId());
        Faq saved = faqRepository.save(faq);
        return new FaqStatus(saved.getStatus());
    }

    private Faq tryGetNotice(Integer faqId) {
        return faqRepository.findById(faqId)
                               .orElseThrow(() -> new FaqNotFoundException(faqId));
    }
}

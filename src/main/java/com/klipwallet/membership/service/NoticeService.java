package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.klipwallet.membership.dto.notice.NoticeDto;
import com.klipwallet.membership.dto.notice.NoticeDto.Summary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MainNoticeActivated;
import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.entity.NoticeUpdatable;
import com.klipwallet.membership.exception.NoticeNotFoundException;
import com.klipwallet.membership.repository.NoticeRepository;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeAssembler noticeAssembler;

    /**
     * 공지사항 생성
     *
     * @param command 생성할 공지사항 내용
     * @param user    생성자
     * @return 생성된 공지사항 요약
     */
    @Transactional
    public NoticeDto.Summary create(NoticeDto.Create command, AuthenticatedUser user) {
        Notice entity = command.toNotice(user);
        Notice saved = noticeRepository.save(entity);
        return new Summary(saved);
    }

    /**
     * 공지사항 1건 상세조회
     *
     * @param noticeId 조회할 공지사항 ID
     * @return 공지사항 상세 DTO
     */
    @Transactional(readOnly = true)
    public NoticeDto.Detail getDetail(Integer noticeId) {
        Notice notice = tryGetNotice(noticeId);
        return noticeAssembler.toDetail(notice);
    }

    private Notice tryGetNotice(Integer noticeId) {
        return noticeRepository.findById(noticeId)
                               .orElseThrow(() -> new NoticeNotFoundException(noticeId));
    }

    /**
     * 공지사항 수정
     * <p>
     * 수정 시 메인 노출을 활성화 시킨 설정이 있디면 {@link #subscribeMainNoticeActivated(com.klipwallet.membership.entity.MainNoticeActivated)}를 통해서
     * 해당 공지사항 이외에 다른 공지사항의 메인 노출을 비활성화 시킵니다.
     * </p>
     *
     * @param noticeId 수정할 공지사항 ID
     * @param command  수정할 공지사항 내용
     * @param user     수정자
     * @return 수정된 공지사항 상세
     */
    @Transactional
    public NoticeDto.Detail update(Integer noticeId, NoticeDto.Update command, AuthenticatedUser user) {
        NoticeUpdatable updatable = command.toUpdatable(user);
        Notice notice = tryGetNotice(noticeId);

        notice.update(updatable);
        Notice saved = noticeRepository.save(notice);

        return noticeAssembler.toDetail(saved);
    }

    /**
     * 공지사항 메인 노출 활성화 시 후처리를 위한 이벤트 구독
     *
     * <p>
     * 기존에 메인으로 선정된 공지사항을 메인 비노출로 처리.
     * 기존 트랜잭션에 포함시키기 위해서 {@code BEFORE_COMMIT}으로 처리
     * </p>
     *
     * @param event 공지사항 메인 노출 활성화 이벤트
     * @see com.klipwallet.membership.entity.MainNoticeActivated
     * @see #update(Integer, com.klipwallet.membership.dto.notice.NoticeDto.Update, com.klipwallet.membership.entity.AuthenticatedUser)
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void subscribeMainNoticeActivated(MainNoticeActivated event) {
        Integer mainNoticeId = event.getNoticeId();
        List<Notice> mainNotices = noticeRepository.findAllByMain(true);
        for (Notice notice : mainNotices) {
            if (notice.equalId(mainNoticeId)) { // 현재 활성화된 메인 공지는 제외!
                continue;
            }
            notice.deactivateMain();
            noticeRepository.save(notice);
        }
    }

}

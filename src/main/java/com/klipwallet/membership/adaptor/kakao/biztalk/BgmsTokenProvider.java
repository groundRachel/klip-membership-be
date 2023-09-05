package com.klipwallet.membership.adaptor.kakao.biztalk;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.config.BgmsProperties;
import com.klipwallet.membership.exception.InternalApiException;

@Slf4j
@RequiredArgsConstructor
@Component
public class BgmsTokenProvider {
    private final BgmsProperties bgmsProperties;
    private final BgmsApiClient bgmsApiClient;
    private final ApplicationEventPublisher eventPublisher;

    private final AtomicReference<BgmsToken> atomicToken = new AtomicReference<>();
    private final ReentrantLock pending = new ReentrantLock();

    /**
     * BGMS 통신을 위한 사용자 토큰을 준비함. 만약 토큰이 만료되지 않았으면, 그것을 그대로 사용함.
     * <p>
     * 기본(=MAX) 사용자 토큰의 만료시간은 24시간.
     * </p>
     */
    BgmsToken prepareToken() {
        return findValidToken().orElseGet(this::newTokenOnLock);
    }

    private Optional<BgmsToken> findValidToken() {
        return Optional.of(this.atomicToken)
                       .map(AtomicReference::getAcquire)
                       .filter(this::isValidToken);
    }

    @NonNull
    private BgmsToken newTokenOnLock() {
        pending.lock();    // 최후의 lock;
        try {
            return findValidToken().orElseGet(this::reissueToken);
        } finally {
            pending.unlock();
        }
    }

    @NonNull
    private BgmsToken reissueToken() {
        LocalDateTime expiredAt = LocalDateTime.now().plusHours(24L);   // 요청 전에 만료일시를 미리 준비한다. (기본 24시간 설정)
        BgmsTokenReq req = BgmsTokenReq.expiredMax(bgmsProperties.getId(), bgmsProperties.getPassword());
        BgmsTokenRes res = bgmsApiClient.getToken(req);
        if (!res.isSuccessful()) {
            throw InternalApiException.biztalk(res);
        }
        BgmsToken newToken = new BgmsToken(res.getToken(), expiredAt);
        atomicToken.setRelease(newToken);
        return newToken;
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean isValidToken(BgmsToken token) {
        if (!token.exists()) {
            return false;
        }
        if (token.isExpiredSoon()) {
            eventPublisher.publishEvent(new BgmsTokenExpiredSoon(token));
        }
        if (token.isExpired()) {
            return false;
        }
        return true;
    }

    /**
     * 사용자 토큰이 곧 만료되므로, background(Async)로 재발급을 시도한다.
     */
    @Async
    @EventListener(BgmsTokenExpiredSoon.class)
    public void subscribeBgmsTokenExpiredSoon(BgmsTokenExpiredSoon event) {
        if (pending.isLocked()) {  // 이미 새로운 토큰을 받기 위한 처리 중.
            log.warn("Ignore subscribeBgmsTokenExpiredSoon. Because Pending. oldToken: {}", event.getOldToken());
            return;
        }
        log.info("Try to subscribeBgmsTokenExpiredSoon. oldToken: {}", event.getOldToken());
        try {
            BgmsToken newToken = newTokenOnLock();
            log.info("Success to subscribeBgmsTokenExpiredSoon: newToken: {}", newToken);
        } catch (Exception cause) {
            log.error("[FATAL] Fail to subscribeBgmsTokenExpiredSoon: oldToken: {}", event.getOldToken());
        }
    }
}

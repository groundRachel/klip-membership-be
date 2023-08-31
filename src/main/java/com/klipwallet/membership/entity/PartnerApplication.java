package com.klipwallet.membership.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.klipwallet.membership.exception.member.PartnerApplicationAlreadyProcessedException;

import static com.klipwallet.membership.entity.PartnerApplication.Status.*;

@Entity
@Getter
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
public class PartnerApplication extends AbstractAggregateRoot<PartnerApplication> {
    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String oauthId;
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String businessName;
    @Column(nullable = false)
    private String phoneNumber;
    // NOTE : 사업자번호, 이메일은 not unique
    // 이유: admin이 요청 거절 시, DB에 데이터는 남아 있으며 같은 email로 재요청 가능
    @Column(nullable = false)
    private String businessRegistrationNumber;
    private Status status;
    private String rejectReason;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private MemberId processorId;

    public PartnerApplication(String businessName, String phoneNumber, String businessRegistrationNumber, String email, String oauthId) {
        this.businessName = businessName;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.status = Status.APPLIED;
        this.email = email;
        this.oauthId = oauthId;
        this.createdAt = LocalDateTime.now();
    }

    public PartnerApplication() {
    }

    private void processedBy(@NonNull MemberId processorId) {
        this.processorId = processorId;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 파트너 신청 승인됨 -> 파트너 생성
     *
     * @param processorId 처리자 ID
     * @return canSkipDuplicatedRequest
     * @see com.klipwallet.membership.entity.PartnerApplicationApproved
     */
    public boolean approve(MemberId processorId) {
        if (canSkipDuplicatedRequest(APPROVED, processorId)) {
            return true;
        }
        checkProcessable();

        this.status = Status.APPROVED;
        processedBy(processorId);
        registerEvent(new PartnerApplicationApproved(this.email));

        return false;
    }

    /**
     * 파트너 신청 거절.
     *
     * @param rejectReason 거절 사유
     * @param processorId  처리자 ID
     * @return canSkipDuplicatedRequest
     * @see com.klipwallet.membership.entity.PartnerApplicationRejected
     */
    public boolean reject(String rejectReason, MemberId processorId) {
        if (canSkipDuplicatedRequest(REJECTED, processorId)) {
            return true;
        }
        checkProcessable();

        this.status = Status.REJECTED;
        this.rejectReason = rejectReason;
        processedBy(processorId);
        registerEvent(new PartnerApplicationRejected(this.email, rejectReason));

        return false;
    }

    private boolean canSkipDuplicatedRequest(Status expectedStatus, MemberId expectedUpdatedId) {
        // TODO WINNIE testcode
        return getStatus() == expectedStatus && getProcessorId() == expectedUpdatedId;
    }

    private void checkProcessable() {
        if (getStatus() != APPLIED) {
            throw new PartnerApplicationAlreadyProcessedException(this);
        }
    }

    @Getter
    @Schema(name = "PartnerApplication.Status", description = "파트너 가입 요청 상태", example = "approved")
    public enum Status implements Statusable {
        APPLIED(1),
        APPROVED(2),
        REJECTED(3);

        private final byte code;

        Status(int code) {
            this.code = Statusable.requireVerifiedCode(code);
        }

        @JsonCreator
        @Nullable
        public static Status fromDisplay(String display) {
            return Statusable.fromDisplay(Status.class, display);
        }

        @JsonValue
        @Override
        public String toDisplay() {
            return Statusable.super.toDisplay();
        }
    }
}

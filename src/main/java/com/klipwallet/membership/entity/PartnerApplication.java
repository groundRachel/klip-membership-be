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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Entity
@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class PartnerApplication {
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

    @Column(nullable = false)
    String email;
    @Column(nullable = false)
    String oAuthId;

    private Status status;
    private String rejectReason;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private MemberId processorId;

    public PartnerApplication(String businessName, String phoneNumber, String businessRegistrationNumber, String email, String oAuthId) {
        this.businessName = businessName;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.status = Status.APPLIED;
        this.email = email;
        this.oAuthId = oAuthId;
        this.createdAt = LocalDateTime.now();
    }

    public PartnerApplication() {
    }

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

        public byte getCode() {
            return this.code;
        }

        @JsonValue
        @Override
        public String toDisplay() {
            return Statusable.super.toDisplay();
        }
    }

    private void processedBy(@NonNull MemberId processorId) {
        this.processorId = processorId;
        this.processedAt = LocalDateTime.now();
    }

    public PartnerApplication approve(MemberId processor) {
        this.status = Status.APPROVED;
        processedBy(processor);
        return this;
    }

    public void reject(String rejectReason, MemberId processor) {
        this.status = Status.REJECTED;
        this.rejectReason = rejectReason;
        processedBy(processor);
    }
}

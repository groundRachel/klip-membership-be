package com.klipwallet.membership.entity;

import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Entity
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AppliedPartner extends Member {

    private String name;
    private String phoneNumber;
    private String businessRegistrationNumber;
    @Setter
    private Status status;
    @Setter
    private String rejectReason;

    public AppliedPartner(String name, String phoneNumber, String businessRegistrationNumber, String email, @NonNull String oAuthId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.status = Status.APPLIED;
        this.email = email;
        this.oAuthId = oAuthId;
    }

    public AppliedPartner() {
    }

    @Schema(name = "AppliedPartner.Status", description = "파트너 가입 요청 상태", example = "approved")
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

    public Partner toApprovedPartner() {
        return new Partner(name, phoneNumber, businessRegistrationNumber, email, oAuthId);
    }
}

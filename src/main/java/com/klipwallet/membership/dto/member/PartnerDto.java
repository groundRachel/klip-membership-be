package com.klipwallet.membership.dto.member;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import com.klipwallet.membership.entity.AppliedPartner;

@RequiredArgsConstructor
public class PartnerDto {
    public record Apply(@NotBlank String name,
                        @NotBlank @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$") String phoneNumber,
                        @NotBlank @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$") String businessRegistrationNumber,
                        @NotBlank @Email String email,
                        @NotBlank String oAuthId) {
        public AppliedPartner toAppliedPartner() {
            return new AppliedPartner(name, phoneNumber, businessRegistrationNumber, email, oAuthId);
        }
    }

    public record ApplyResult(
            @NonNull Integer id,
            @NonNull String name,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}

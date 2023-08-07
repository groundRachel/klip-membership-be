package com.klipwallet.membership.dto.member;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.RequiredArgsConstructor;

import com.klipwallet.membership.entity.Partner;

@RequiredArgsConstructor
public class PartnerDto {
    public record Apply(@NotBlank String name,
                        @NotBlank String phoneNumber,
                        @NotBlank String businessRegistrationNumber,
                        @NotBlank @Email String email,
                        @NotBlank String oAuthID) {
        public Partner toPartner() {
            return new Partner(name, phoneNumber, businessRegistrationNumber, email, oAuthID);
        }
    }

    public record ApplyResult(
            @NotBlank Integer id,
            LocalDateTime createdAt,

            LocalDateTime updatedAt
    ) {}
}

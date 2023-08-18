package com.klipwallet.membership.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.klipwallet.membership.entity.AppliedPartner;

public interface AppliedPartnerRepository extends JpaRepository<AppliedPartner, Integer>, JpaSpecificationExecutor<AppliedPartner> {
    Optional<AppliedPartner> findByBusinessRegistrationNumber(String businessRegistrationNumber);
}

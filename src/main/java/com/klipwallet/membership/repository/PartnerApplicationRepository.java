package com.klipwallet.membership.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.klipwallet.membership.entity.PartnerApplication;

public interface PartnerApplicationRepository extends JpaRepository<PartnerApplication, Integer>, JpaSpecificationExecutor<PartnerApplication> {
    Optional<PartnerApplication> findByBusinessRegistrationNumber(String businessRegistrationNumber);
}

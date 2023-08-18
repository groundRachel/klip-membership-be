package com.klipwallet.membership.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.klipwallet.membership.entity.Partner;

public interface PartnerRepository extends JpaRepository<Partner, Integer>, JpaSpecificationExecutor<Partner> {

    Optional<Partner> findByBusinessRegistrationNumber(String businessRegistrationNumber);
}

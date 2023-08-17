package com.klipwallet.membership.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.klipwallet.membership.entity.AcceptedPartner;
import com.klipwallet.membership.entity.AppliedPartner;

public interface AcceptedPartnerRepository extends JpaRepository<AcceptedPartner, Integer>, JpaSpecificationExecutor<AcceptedPartner> {

    AcceptedPartner findByBusinessRegistrationNumber(String businessRegistrationNumber);
}

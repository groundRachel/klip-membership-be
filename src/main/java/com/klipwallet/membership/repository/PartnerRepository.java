package com.klipwallet.membership.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.Partner.PartnerSummary;
import com.klipwallet.membership.entity.PartnerApplication;

public interface PartnerRepository extends JpaRepository<Partner, Integer>, JpaSpecificationExecutor<Partner> {

    Optional<Partner> findByBusinessRegistrationNumber(String businessRegistrationNumber);

    @Query("""
           select p.id as id, p.name as name, a.processedAt as processedAt, a.processorId as processorId
           from Partner p left join PartnerApplication a on p.businessRegistrationNumber = a.businessRegistrationNumber
           where a.status = :status""")
    Page<PartnerSummary> findAllPartners(PartnerApplication.Status status, Pageable pageable);
}

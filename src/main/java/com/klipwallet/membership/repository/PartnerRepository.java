package com.klipwallet.membership.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.Partner.PartnerSummaryView;
import com.klipwallet.membership.entity.PartnerApplication;

public interface PartnerRepository extends JpaRepository<Partner, Integer>, JpaSpecificationExecutor<Partner> {

    Optional<Partner> findByBusinessRegistrationNumber(String businessRegistrationNumber);

    @Query("""
           select new com.klipwallet.membership.entity.MemberId(p.id) as memberId, p.name as name, a.processedAt as processedAt, a.processorId as processorId
           from Partner p left join PartnerApplication a on p.businessRegistrationNumber = a.businessRegistrationNumber
           where a.status = :status""")
    Page<PartnerSummaryView> findAllPartners(@Param("status") PartnerApplication.Status status, Pageable pageable);
}

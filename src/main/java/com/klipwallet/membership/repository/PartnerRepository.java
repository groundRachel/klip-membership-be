package com.klipwallet.membership.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.klipwallet.membership.entity.Member.Status;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerDetailView;
import com.klipwallet.membership.entity.PartnerSummaryView;

public interface PartnerRepository extends JpaRepository<Partner, Integer>, JpaSpecificationExecutor<Partner> {

    Optional<Partner> findByBusinessRegistrationNumber(String businessRegistrationNumber);

    Optional<Partner> findByOauthId(String oauthId);

    Optional<Partner> findByEmail(String email);

    // TODO join on partner application id (foreign key)
    @Query("""
           select p.id as memberId, p.name as name, a.processedAt as processedAt, a.processorId as processorId
             from Partner p left join PartnerApplication a on p.businessRegistrationNumber = a.businessRegistrationNumber
            where a.status = :status
           """)
    Page<PartnerSummaryView> findAllPartners(@Param("status") PartnerApplication.Status status, Pageable pageable);

    @Query("""
            select p.id as id, p.name as name, p.businessRegistrationNumber as businessRegistrationNumber, p.email as email, p.createdAt as createdAt, p.klipDropsPartnerId as klipDropsPartnerId, a.processedAt as processedAt,
                  a.processorId as processorId
            from Partner p inner join PartnerApplication a on p.partnerApplicationId = a.id
            where p.id = :partnerId
           """)
    Optional<PartnerDetailView> findPartnerDetailById(@Param("partnerId") Integer partnerId);

    @Query("""
           select klipDropsPartnerId
           from Partner
           where klipDropsPartnerId > 0
           """)
    Set<Integer> findAllKlipDropsIds();

    boolean existsByEmailAndStatusIn(String email, Set<Status> statuses);
}

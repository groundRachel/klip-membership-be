package com.klipwallet.membership.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.klipwallet.membership.entity.PartnerApplication;
import com.klipwallet.membership.entity.PartnerApplication.Status;

public interface PartnerApplicationRepository extends JpaRepository<PartnerApplication, Integer>, JpaSpecificationExecutor<PartnerApplication>,
                                                      CrudRepository<PartnerApplication, Integer> {
    //TODO remove findByBusinessRegistrationNumber, it can be duplicated
    Optional<PartnerApplication> findByBusinessRegistrationNumber(String businessRegistrationNumber);

    Optional<PartnerApplication> findByEmailAndStatusIsIn(String email, List<Status> status);

    boolean existsByEmailAndStatusIsIn(String email, List<Status> status);

    Page<PartnerApplication> findAllByStatus(PartnerApplication.Status status, Pageable pageable);

    Long countByStatus(PartnerApplication.Status status);
}

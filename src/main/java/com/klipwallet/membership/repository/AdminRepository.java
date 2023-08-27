package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}

package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}

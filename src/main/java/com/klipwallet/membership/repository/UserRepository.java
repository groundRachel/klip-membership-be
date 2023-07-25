package com.klipwallet.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.User;
import com.klipwallet.membership.entity.UserId;

public interface UserRepository extends JpaRepository<User, UserId> {

}

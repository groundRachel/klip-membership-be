package com.klipwallet.membership.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findAllByMain(boolean main);
}

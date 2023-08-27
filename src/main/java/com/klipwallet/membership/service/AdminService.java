package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.admin.AdminDto.Register;
import com.klipwallet.membership.dto.admin.AdminDto.Summary;
import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.repository.AdminRepository;

/**
 * 어드민 서비스
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;

    /**
     * 어드민 등록
     * <p>
     * 우선 GroundX 임직원의 이메일을 등록한다.
     * </p>
     *
     * @param command 등록을 위한 인자 DTO
     * @param user    등록자(슈퍼어드민)
     * @return 등록된 어드민 요약 DTO
     */
    @Transactional
    public Summary register(Register command, AuthenticatedUser user) {
        Admin entity = command.toAdmin(user);
        Admin persisted = adminRepository.save(entity);
        return new Summary(persisted);
    }
}

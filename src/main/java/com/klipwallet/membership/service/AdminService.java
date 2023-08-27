package com.klipwallet.membership.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.admin.AdminDto.Register;
import com.klipwallet.membership.dto.admin.AdminDto.Row;
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
    private final AdminAssembler adminAssembler;

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

    /**
     * 어드민 목록
     * <pre>
     *     order by id desc
     * </pre>
     *
     * @return 전체 어드민 목록
     */
    @Transactional(readOnly = true)
    public List<Row> getList() {
        // order by id desc
        Sort listSort = Sort.sort(Admin.class).by(Admin::getId).descending();
        List<Admin> admins = adminRepository.findAll(listSort);
        return adminAssembler.toRows(admins);
    }
}

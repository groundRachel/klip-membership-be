package com.klipwallet.membership.service;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.admin.AdminAssembler;
import com.klipwallet.membership.dto.admin.AdminDto;
import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.Member;
import com.klipwallet.membership.exception.member.AdminNotFoundException;
import com.klipwallet.membership.repository.AdminRepository;

/**
 * 어드민 서비스
 */
@Service
@Slf4j
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
    public AdminDto.Summary register(AdminDto.Register command, AuthenticatedUser user) {
        Admin entity = command.toAdmin(user);
        Admin persisted = adminRepository.save(entity);
        return new AdminDto.Summary(persisted);
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
    public List<AdminDto.Row> getList() {
        // order by id desc
        Sort listSort = Sort.sort(Admin.class).by(Admin::getId).descending();
        List<Admin> admins = adminRepository.findAll(listSort);
        return adminAssembler.toRows(admins);
    }

    /**
     * 어드민 상세
     *
     * @param adminId 조회할 어드민 ID
     * @return 어드민 상세 DTO
     */
    @Transactional(readOnly = true)
    public AdminDto.Detail getDetail(Integer adminId) {
        Admin admin = tryGetAdmin(adminId);
        return adminAssembler.toDetail(admin);
    }

    private Admin tryGetAdmin(@NonNull Integer adminId) {
        return adminRepository.findById(adminId)
                              .filter(Member::isEnabled)
                              .orElseThrow(() -> new AdminNotFoundException(adminId));
    }

    private Admin tryGetAdmin(@NonNull String email) {
        return adminRepository.findByEmail(email)
                              .filter(Member::isEnabled)
                              .orElseThrow(() -> new AdminNotFoundException(email));
    }

    /**
     * 어드민 인증
     * <p>
     * 최초 인증 시 회원가입을 완료 시킴
     * </p>
     *
     * @param oauth2User 인증
     */
    @Transactional
    public Admin signIn(@NonNull AuthenticatedUser oauth2User) {
        String email = oauth2User.getEmail();
        Admin admin = tryGetAdmin(email);
        if (admin.isSignUp()) {
            return admin;
        }
        admin.signUp(oauth2User.getName());
        return adminRepository.save(admin);
    }

    /**
     * 멤버 탈퇴
     * <p>
     * 멤버 논리적 삭제
     * </p>
     */
    @Transactional
    public void withdraw(@NonNull Integer adminId, AuthenticatedUser deleter) {
        try {
            Admin admin = tryGetAdmin(adminId);
            admin.withdraw(deleter.getMemberId());
            adminRepository.save(admin);
        } catch (AdminNotFoundException cause) {
            // Ignore: 존재하지 않는 것은 이미 탈퇴된 것이라서 멱등하게 처리
            log.warn("Admin is already deleted: {}", adminId, cause);
        }
    }
}

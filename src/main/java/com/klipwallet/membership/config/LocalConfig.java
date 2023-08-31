package com.klipwallet.membership.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.klipwallet.membership.dto.admin.AdminDto.Register;
import com.klipwallet.membership.dto.admin.AdminDto.Summary;
import com.klipwallet.membership.entity.Admin;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.NotFoundException;
import com.klipwallet.membership.repository.AdminRepository;
import com.klipwallet.membership.service.AdminService;

/**
 * local 환경에서 개발 편의성을 위해서 미리 회원가입 시키는 구성
 */
@Profile("local")
@Configuration
@EnableConfigurationProperties(DeveloperProperties.class)
@RequiredArgsConstructor
public class LocalConfig implements CommandLineRunner {
    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final DeveloperProperties developerProperties;

    @Transactional
    @Override
    public void run(String... args) {
        Admin superAdmin = registerSuperAdmin();
        for (String email : developerProperties.getEmails()) {
            registerAdmin(email, superAdmin);
        }
    }

    private void registerAdmin(String mail, Admin superAdmin) {
        Register command = new Register(mail);
        adminService.register(command, superAdmin.getMemberId());
    }

    private Admin registerSuperAdmin() {
        Register command = new Register(developerProperties.getSuperAdmin());
        Summary registered = adminService.register(command, new MemberId(1));
        Admin admin = adminRepository.findById(registered.id().value())
                                     .orElseThrow(NotFoundException::new);
        admin.assignSuper();
        return adminRepository.save(admin);
    }
}

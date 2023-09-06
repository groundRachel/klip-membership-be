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
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.exception.NotFoundException;
import com.klipwallet.membership.repository.AdminRepository;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.service.AdminService;

/**
 * local 환경에서 개발 편의성을 위해서 미리 회원가입 시키는 구성
 */
@Profile({"local", "local-dev", "dev"}) // FIXME @Jordan dev 환경에 RDS를 붙이기 전까지 local-dev, dev도 유지한다.
@Configuration
@EnableConfigurationProperties(DeveloperProperties.class)
@RequiredArgsConstructor
public class LocalConfig implements CommandLineRunner {
    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final PartnerRepository partnerRepository;
    private final DeveloperProperties developerProperties;

    @Transactional
    @Override
    public void run(String... args) {
        Admin superAdmin = registerSuperAdmin();
        for (String email : developerProperties.getEmails()) {
            registerAdmin(email, superAdmin);
        }
        registerPartner(superAdmin);
    }

    private void registerPartner(Admin superAdmin) {
        Partner jordan = new Partner("jordan.jung", "010-1111-2222", "000-00-00000",
                                     "jordan.jung@groundx.xyz", "115419318504487812056", superAdmin.getMemberId());
        partnerRepository.save(jordan);
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

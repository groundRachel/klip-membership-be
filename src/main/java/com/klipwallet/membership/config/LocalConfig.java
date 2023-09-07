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
        // TODO @Jordan properties로 분리
        Partner jordan = new Partner(1, 11, "jordan.jung", "010-2738-3987", "000-00-00000",
                                     "jordan.jung@groundx.xyz", "115419318504487812056", superAdmin.getMemberId());
        partnerRepository.save(jordan);
        Partner rachel = new Partner(2, 12, "rachel.lee", "010-7289-7072", "000-00-11111",
                                     "rachel.lee@groundx.xyz", "105434102953836621752", superAdmin.getMemberId());
        partnerRepository.save(rachel);

        Partner sello = new Partner(3, 13, "sello.lee", "010-4433-4405", "000-00-22222",
                                    "sello.lee@groundx.xyz", "100531700171845510030", superAdmin.getMemberId());
        partnerRepository.save(sello);

        Partner twinsen = new Partner(4, 14, "twinsen.kim", "010-9787-2848", "000-00-33333",
                                    "twinsen.kim@groundx.xyz", "116227101269935559902", superAdmin.getMemberId());
        partnerRepository.save(twinsen);

        Partner ian = new Partner(5, 15, "ian.han", "010-3201-6272", "000-00-44444",
                                      "ian.han@groundx.xyz", "105028407626939104183", superAdmin.getMemberId());
        partnerRepository.save(ian);

        Partner winnie = new Partner(6, 16, "winnie.byun", "010-2955-4858", "000-00-55555",
                                  "winnie.byun@groundx.xyz", "116061336006046612273", superAdmin.getMemberId());
        partnerRepository.save(winnie);

        Partner ted = new Partner(7, 17, "ted.jeong", "010-6440-2491", "000-00-666666",
                                     "ted.jeong@groundx.xyz", "105828933857671458608", superAdmin.getMemberId());
        partnerRepository.save(ted);
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

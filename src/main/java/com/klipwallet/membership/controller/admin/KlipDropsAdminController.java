package com.klipwallet.membership.controller.admin;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.klipdrops.KlipDropsDto.Partner;
import com.klipwallet.membership.service.KlipDropsPartnerService;

@Tag(name = "Admin.KlipDrops", description = "Klip Drops API")
@RestController
@RequestMapping("/admin/v1/klipdrops-partners")
@RequiredArgsConstructor
public class KlipDropsAdminController {
    private final KlipDropsPartnerService klipDropsPartnerService;

    @Operation(summary = "Klip Drops 파트너 목록 조회",
               description = "Klip Drops 파트너 ID 변경을 위한 목록 조회. 현재 prod 환경은 partner 수가 200개 미만임. 따라서 1회로 모든 데이터를 조회함.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public List<Partner> getKlipDropsPartners(@RequestParam(required = false) String search) {
        return klipDropsPartnerService.getKlipDropsPartners(search);
    }
}

package com.klipwallet.membership.controller.tool;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klipwallet.membership.dto.nft.NftDto.NftSummary;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.service.NftService;

@Tag(name = "Tool.NFT", description = "Tool NFT API")
@RestController
@RequestMapping("/tool/v1/nfts")
@RequiredArgsConstructor
public class NftToolController {
    private final NftService nftService;

    @Operation(summary = "오픈채팅방 생성을 위한 NFT 목록 조회")
    @Description(value = "Klip Drops에서 해당하는 Drops 최대 1000개 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping
    public List<NftSummary> getNftList(@AuthenticationPrincipal AuthenticatedUser user) {
        return nftService.getNftList(user.getMemberId());
    }
}

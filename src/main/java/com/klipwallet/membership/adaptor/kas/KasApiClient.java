package com.klipwallet.membership.adaptor.kas;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.klipwallet.membership.adaptor.kas.dto.GetNftTokenRes;
import com.klipwallet.membership.adaptor.kas.feign.KasFeignConfig;

@FeignClient(name = "kas", configuration = KasFeignConfig.class)
public interface KasApiClient {
    @GetMapping(value = "/v2/contract/nft/{address}/token/{token-id}")
    GetNftTokenRes getNftToken(@PathVariable("address") String nftSCA, @PathVariable(value = "token-id") String tokenId);
}
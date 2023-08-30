package com.klipwallet.membership.adaptor.klipdrops;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.klipwallet.membership.adaptor.klipdrops.dto.Drop;
import com.klipwallet.membership.adaptor.klipdrops.dto.Drops;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartner;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartners;

@FeignClient(name = "klip-drops-internal")
public interface KlipDropsInternalApiClient {
    @GetMapping(value = "/v1/partners/{businessRegistrationNumber}")
    KlipDropsPartner getPartnerByBusinessNumber(@PathVariable(value = "businessRegistrationNumber") String businessRegistrationNumber);

    @GetMapping(value = "/v1/partners")
    KlipDropsPartners getAllPartners(@RequestParam(value = "search") String search, // 검색하고자 하는 파트너 이름 혹은 파트너 번호 일부 입력
                                     @RequestParam(value = "cursor") String cursor,
                                     @RequestParam(value = "size", required = false) Integer size);

    @GetMapping(value = "/v1/partner/{partnerId}/drops")
    Drops getDropsByPartner(@RequestParam(value = "partnerId") Integer partnerId,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "size", required = false) Integer size);

    @GetMapping(value = "/v1/drops")
    List<Drop> getDropsByIds();
}

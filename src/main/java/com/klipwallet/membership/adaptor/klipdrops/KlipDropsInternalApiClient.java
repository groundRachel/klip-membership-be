package com.klipwallet.membership.adaptor.klipdrops;

import java.math.BigInteger;
import java.util.List;

import org.springframework.cloud.openfeign.CollectionFormat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrop;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsDrops;
import com.klipwallet.membership.adaptor.klipdrops.dto.KlipDropsPartners;

@FeignClient(name = "klip-drops-internal")
public interface KlipDropsInternalApiClient {
    @GetMapping(value = "/v1/partners")
    KlipDropsPartners getAllPartners(@RequestParam(value = "id") Integer klipDropsPartnerId, // TODO test after implementation is done at Drops
                                     @RequestParam(value = "businessRegistrationNumber") String businessRegistrationNumber,
                                     @RequestParam(value = "search") String search, // 검색하고자 하는 파트너 이름 혹은 파트너 번호 일부 입력
                                     @RequestParam(value = "cursor") String cursor,
                                     @RequestParam(value = "size", required = false) Integer size);

    @GetMapping(value = "/v1/partners/{partnerId}/drops")
    KlipDropsDrops getDropsByPartner(@PathVariable(value = "partnerId") Integer partnerId,
                                     @RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "size", required = false) Integer size);

    // TODO [Drops] Consider changing this PUT
    @Description(value = "최대길이 100")
    @GetMapping(value = "/v1/drops")
    @CollectionFormat(feign.CollectionFormat.CSV)
    List<KlipDropsDrop> getDropsByIds(@RequestParam(value = "drop_ids") List<BigInteger> dropIds);
}

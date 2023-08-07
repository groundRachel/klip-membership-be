package com.klipwallet.membership.adaptor.kakao;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.klipwallet.membership.config.FeignConfig;

@FeignClient(name = "kakao", url = "${application.kakao-api.openlink-url}", configuration = {FeignConfig.class})
public interface KakaoApiClient {

    // TODO: linkIds Long[]을 받아서 String [1,2,3]으로 변환하는 CustomFormatter 구현
    @GetMapping(value = "/list/link")
    List<GetOpenlinkResponseItem> getOpenlink(@RequestParam("domain_id") Long domainId,
                                              @RequestParam(value = "link_ids", required = false) String linkIds);

    @PostMapping(value = "/create/link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ShortOpenlinkResp createOpenlink(CreateOpenlinkReqDto createOpenlinkReqDto);

    @PostMapping(value = "/update/link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ShortOpenlinkResp updateOpenlink(UpdateOpenlinkReqDto updateOpenlinkReqDto);

    @DeleteMapping(value = "/delete/link")
    ShortOpenlinkResp deleteOpenlink(@RequestParam("target_id") String targetId, @RequestParam("target_id_type") String target,
                                     @RequestParam("domain_id") Long domainId, @RequestParam("link_id") Long linkId);

    @PostMapping(value = "/join/link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    JoinOpenlinkResp joinOpenlink(JoinOpenlinkReqDto joinOpenlinkReqDto);

    @PostMapping(value = "/leave/link", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LinkUserOpenlinkResp leaveOpenlink(LeaveOpenlinkReqDto leaveOpenlinkReqDto);

    @PostMapping(value = "/update/profile", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LinkUserOpenlinkResp updateProfile(UpdateProfileReqDto updateProfileReqDto);
}

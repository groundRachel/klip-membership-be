package com.klipwallet.membership.service;

import org.springframework.stereotype.Component;

import com.klipwallet.membership.entity.Attachable;

@Component
public class StorageHelper {

    @SuppressWarnings("unused")
    public String toLinkUrl(Attachable command) {
        return "https://klip-media.dev.klaytn.com/klip/membership/editor/images/e8ff69e6-20f6-47d1-8dc1-08342e9af129";
    }
}

package com.klipwallet.membership.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.TokenId;
import com.klipwallet.membership.entity.kas.NftToken;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final KasService kasService;

    public boolean isTokenOwner(Address sca, TokenId tokenId, Address klaytnAddress) {
        NftToken token = kasService.getNftToken(sca, tokenId);
        return token.getOwner().equalsIgnoreCase(klaytnAddress.getValue());
    }
}

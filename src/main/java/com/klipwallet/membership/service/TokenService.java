package com.klipwallet.membership.service;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.TokenId;
import com.klipwallet.membership.entity.kas.NftToken;

public interface TokenService {
    /**
     * Nft Token 정보 조회
     *
     * @param sca     Smart Contract Address
     * @param tokenId tokenId
     * @return Nft Token
     */
    NftToken getNftToken(Address sca, TokenId tokenId);
}

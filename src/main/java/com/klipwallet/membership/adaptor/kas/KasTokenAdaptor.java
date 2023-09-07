package com.klipwallet.membership.adaptor.kas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.klipwallet.membership.adaptor.kas.dto.GetNftTokenRes;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.TokenId;
import com.klipwallet.membership.entity.kas.NftToken;
import com.klipwallet.membership.service.TokenService;


@Component
@Slf4j
@RequiredArgsConstructor
public class KasTokenAdaptor implements TokenService {

    private final KasApiClient apiClient;

    @Override
    public NftToken getNftToken(Address sca, TokenId tokenId) {
        GetNftTokenRes nftToken = apiClient.getNftToken(sca.getValue(), tokenId.getHexString());
        return new NftToken(nftToken.tokenId(), nftToken.owner(), nftToken.previousOwner(), nftToken.tokenUri(),
                            nftToken.transactionHash(), nftToken.createdAt(), nftToken.updatedAt());
    }
}
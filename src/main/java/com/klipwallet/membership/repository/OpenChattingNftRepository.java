package com.klipwallet.membership.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klipwallet.membership.entity.OpenChattingNft;

public interface OpenChattingNftRepository extends JpaRepository<OpenChattingNft, Long> {

    /**
     * contractAddress와 dropId로 OpenChattingNft 조회
     *
     * @param klipDropsSca   NFT Address
     * @param dropId dropId
     * @return OpenChattingNft
     */
    Optional<OpenChattingNft> findByKlipDropsScaAndDropId(String klipDropsSca, Long dropId);
}

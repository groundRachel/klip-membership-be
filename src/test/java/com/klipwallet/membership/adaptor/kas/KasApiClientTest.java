package com.klipwallet.membership.adaptor.kas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import com.klipwallet.membership.adaptor.kas.dto.GetNftToken;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@Disabled("수동으로 한번만 테스트")
class KasApiClientTest {
    @Autowired
    KasApiClient kasApiClient;
    @Autowired
    KasAdaptor kasAdaptor;

    @Description("test KasApiClient > getNftToken")
    @Test
    void GetNftToken() {
        var testContractAddress = "0xa9a95c5fef43830d5d67156a2582a2e793acb465";
        var testTokenId = "0x919FB9AFEAB";

        GetNftToken resp = kasApiClient.getNftToken(testContractAddress, testTokenId);
        assertThat(resp.tokenId()).isEqualToIgnoringCase(testTokenId);
        assertThat(resp.owner()).isNotEmpty();
        assertThat(resp.previousOwner()).isNotEmpty();
        assertThat(resp.tokenUri()).isNotEmpty();
        assertThat(resp.transactionHash()).isNotEmpty();
        assertThat(resp.createdAt()).isNotZero();
        assertThat(resp.updatedAt()).isNotZero();
    }
}

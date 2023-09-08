package com.klipwallet.membership.adaptor.kas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import com.klipwallet.membership.adaptor.kas.dto.GetNftTokenRes;
import com.klipwallet.membership.exception.kas.KasBadRequestInternalApiException;
import com.klipwallet.membership.exception.kas.KasNotFoundInternalApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ThApiClientTest {
    @Autowired
    ThApiClient thApiClient;

    @Description("test KasApiClient > getNftToken > Success(200)")
    @Test
    void GetNftToken_Success() {
        var testContractAddress = "0xa9a95c5fef43830d5d67156a2582a2e793acb465";
        var testTokenId = "0x919FB9AFEAB";

        GetNftTokenRes resp = thApiClient.getNftToken(testContractAddress, testTokenId);
        assertThat(resp.tokenId()).isEqualToIgnoringCase(testTokenId);
        assertThat(resp.owner()).isNotEmpty();
        assertThat(resp.previousOwner()).isNotEmpty();
        assertThat(resp.tokenUri()).isNotEmpty();
        assertThat(resp.transactionHash()).isNotEmpty();
        assertThat(resp.createdAt()).isNotZero();
        assertThat(resp.updatedAt()).isNotZero();
    }

    @Description("test KasApiClient > getNftToken > NotFound(404)")
    @Test
    void GetNftToken_NotFound() {
        var testContractAddress = "0xa9a95c5fef43830d5d67156a2582a2e793acb465";
        var testTokenId = "0x119FB9AFEAB";
        assertThrows(KasNotFoundInternalApiException.class, () -> thApiClient.getNftToken(testContractAddress, testTokenId));
    }

    @Description("test KasApiClient > getNftToken > BadRequest(400)")
    @Test
    void GetNftToken_BadRequest() {
        var testTokenId = "0x119FB9AFEAB";
        assertThrows(KasBadRequestInternalApiException.class, () -> thApiClient.getNftToken("", testTokenId));
    }
}

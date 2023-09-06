package com.klipwallet.membership.service

import com.klipwallet.membership.entity.Address
import com.klipwallet.membership.entity.TokenId
import com.klipwallet.membership.entity.kas.NftToken
import spock.lang.Specification

class TokenServiceTest extends Specification {
    TokenService tokenService;
    KasService kasService;

    def setup() {
        kasService = Mock()
        tokenService = new TokenService(kasService)
    }

    def "IsTokenOwner"() {
        given:
        def address = new Address("0xa9a95c5fef43830d5d67156a2582a2e793acb465")
        def tokenId = new TokenId("10007200071339");
        def klaytnAddress = new Address("0x34d21b1e550d73cee41151c77f3c73359527a396");
        def token = new NftToken("0x919FB9AFEAB", klaytnAddress.value, "0xcee8faf64bb97a73bb51e115aa89c17ffa8dd167", "", "0x4082a74397164c0f06f8d8d2f3eeff09535e30e6340aa28636b17449eb5b520f", 1693988242, 1693988242);

        1 * kasService.getNftToken(address, tokenId) >> token

        when:
        def result = tokenService.isTokenOwner(address, tokenId, klaytnAddress)

        then:
        result
    }
}

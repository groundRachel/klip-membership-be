package com.klipwallet.membership.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.klipwallet.membership.adaptor.klip.KlipAccount;
import com.klipwallet.membership.dto.openchatting.OpenChattingStatus;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingMember;
import com.klipwallet.membership.entity.OpenChattingMember.Role;
import com.klipwallet.membership.entity.OpenChattingNft;
import com.klipwallet.membership.entity.TokenId;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;
import com.klipwallet.membership.entity.kas.NftToken;
import com.klipwallet.membership.exception.ForbiddenException;
import com.klipwallet.membership.exception.kas.KasNotFoundInternalApiException;
import com.klipwallet.membership.exception.kas.KasUnknownInternalApiException;
import com.klipwallet.membership.repository.OpenChattingMemberRepository;
import com.klipwallet.membership.repository.OpenChattingNftRepository;
import com.klipwallet.membership.repository.OpenChattingRepository;

import static com.klipwallet.membership.exception.ErrorCode.OPENCHAT_ACCESS_DENIED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenChattingServiceTest {
    @Autowired
    OpenChattingService openChattingService;

    @MockBean
    TokenService tokenService;
    @Autowired
    OpenChattingRepository openChattingRepository;
    @Autowired
    OpenChattingNftRepository openChattingNftRepository;
    @Autowired
    OpenChattingMemberRepository openChattingMemberRepository;
    @AfterEach
    void afterEach() {
        openChattingRepository.deleteAll();
        openChattingRepository.flush();
        openChattingNftRepository.deleteAll();
        openChattingNftRepository.flush();
        openChattingMemberRepository.deleteAll();
        openChattingMemberRepository.flush();
    }

    @Test
    void getOpenChattingStatus_kasThInternalServerError() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        TokenId tokenId = new TokenId("1234");
        Address klaytnAddress = new Address("0x23706619a2001aab3cfe83076f0e2d6ec2d8e204");
        KlipUser klipUser = new KlipAccount(1L, "123-1234", "test@gmail.com", "010-1111-1111");
        when(tokenService.getNftToken(contractAddress, tokenId)).thenThrow(KasUnknownInternalApiException.class);

        // when
        try {
            openChattingService.getOpenChattingStatus(contractAddress, tokenId, klaytnAddress, klipUser);
        } catch (Exception e) {
            // then
            assertThat(e).isInstanceOf(KasUnknownInternalApiException.class);
        }
    }

    @Test
    void getOpenChattingStatus_tokenNotFound() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        TokenId tokenId = new TokenId("1234");
        Address klaytnAddress = new Address("0x23706619a2001aab3cfe83076f0e2d6ec2d8e204");
        KlipUser klipUser = new KlipAccount(1L, "123-1234", "test@gmail.com", "010-1111-1111");
        when(tokenService.getNftToken(contractAddress, tokenId)).thenThrow(KasNotFoundInternalApiException.class);

        // when
        try {
            openChattingService.getOpenChattingStatus(contractAddress, tokenId, klaytnAddress, klipUser);
        } catch (ForbiddenException e) {
            // then
            assertThat( e.getErrorCode()).isEqualTo(new ForbiddenException(OPENCHAT_ACCESS_DENIED).getErrorCode());
        }
    }

    @Test
    void getOpenChattingStatus_notOwner() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        TokenId tokenId = new TokenId("1234");
        Address klaytnAddress = new Address("0x23706619a2001aab3cfe83076f0e2d6ec2d8e204");
        KlipUser klipUser = new KlipAccount(1L, "123-1234", "test@gmail.com", "010-1111-1111");
        Address klaytnAddress2 = new Address("0x11106619a2001aab3cfe83076f0e2d6ec2d8e204");
        NftToken nftToken = new NftToken(tokenId.getHexString(), klaytnAddress2.getValue(), klaytnAddress.getValue(), "", "0xc83365ed7fcaddff4e110e53a856f2879f4f8da89874b112c80dbed5d9f222e4", 1694139797, 1694139797);
        when(tokenService.getNftToken(contractAddress, tokenId)).thenReturn(nftToken);

        // when
        try {
            openChattingService.getOpenChattingStatus(contractAddress, tokenId, klaytnAddress, klipUser);
        } catch (ForbiddenException e) {
            // then
            assertThat( e.getErrorCode()).isEqualTo(new ForbiddenException(OPENCHAT_ACCESS_DENIED).getErrorCode());
        }
    }

    @Test
    void getOpenChattingStatus_ChatNotOpen() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        TokenId tokenId = new TokenId("12341222222");
        Address klaytnAddress = new Address("0x23706619a2001aab3cfe83076f0e2d6ec2d8e204");
        KlipUser klipUser = new KlipAccount(1L, "123-1234", "test@gmail.com", "010-1111-1111");
        Address klaytnAddress2 = new Address("0x11106619a2001aab3cfe83076f0e2d6ec2d8e204");
        NftToken nftToken = new NftToken(tokenId.getHexString(), klaytnAddress.getValue(), klaytnAddress2.getValue(), "", "0xc83365ed7fcaddff4e110e53a856f2879f4f8da89874b112c80dbed5d9f222e4", 1694139797, 1694139797);
        when(tokenService.getNftToken(contractAddress, tokenId)).thenReturn(nftToken);

        // when
        OpenChattingStatus status = openChattingService.getOpenChattingStatus(contractAddress, tokenId, klaytnAddress, klipUser);

        // then
        assertThat(status).isEqualTo(new OpenChattingStatus(false, "", false));
    }

    @Test
    void getOpenChattingStatus_OpenChattingJoin() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        TokenId tokenId = new TokenId("12341222222");
        Address klaytnAddress = new Address("0x23706619a2001aab3cfe83076f0e2d6ec2d8e204");
        KlipUser klipUser = new KlipAccount(1L, "123-1234", "test@gmail.com", "010-1111-1111");
        Address klaytnAddress2 = new Address("0x11106619a2001aab3cfe83076f0e2d6ec2d8e204");
        NftToken nftToken = new NftToken(tokenId.getHexString(), klaytnAddress.getValue(), klaytnAddress2.getValue(), "", "0xc83365ed7fcaddff4e110e53a856f2879f4f8da89874b112c80dbed5d9f222e4", 1694139797, 1694139797);
        when(tokenService.getNftToken(contractAddress, tokenId)).thenReturn(nftToken);

        Long dropId =  1234122L;
        MemberId creatorId = new MemberId(1);
        String openChattingUrl = "https://open.kakao.com/o/gvJWpNBf";
        OpenChatting openChatting = new OpenChatting("title", "image", new KakaoOpenlinkSummary(5L, openChattingUrl), contractAddress, creatorId);
        openChatting = openChattingRepository.save(openChatting);
        OpenChattingNft openChattingNft = new OpenChattingNft(openChatting.getId(), dropId, contractAddress, creatorId);
        openChattingNftRepository.save(openChattingNft);

        // when
        OpenChattingStatus status = openChattingService.getOpenChattingStatus(contractAddress, tokenId, klaytnAddress, klipUser);

        // then
        assertThat(status).isEqualTo(new OpenChattingStatus(true, openChattingUrl, false));
    }

    @Test
    void getOpenChattingStatus_OpenChattingJoined() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        Long dropId =  123123412L;
        TokenId tokenId = new TokenId("1231234123134");
        Address klaytnAddress = new Address("0x23706619a2001aab3cfe83076f0e2d6ec2d8e204");
        KlipUser klipUser = new KlipAccount( 77L, "123-1234", "test@gmail.com", "010-1111-1111");
        Address klaytnAddress2 = new Address("0x11106619a2001aab3cfe83076f0e2d6ec2d8e204");
        NftToken nftToken = new NftToken(tokenId.getHexString(), klaytnAddress.getValue(), klaytnAddress2.getValue(), "", "0xc83365ed7fcaddff4e110e53a856f2879f4f8da89874b112c80dbed5d9f222e4", 1694139797, 1694139797);
        MemberId creatorId = new MemberId(1);
        String openChattingUrl = "https://open.kakao.com/o/gvJWpNBf";
        OpenChatting openChatting = new OpenChatting("title", "image", new KakaoOpenlinkSummary(2L, openChattingUrl), contractAddress, creatorId);
        openChatting = openChattingRepository.save(openChatting);
        OpenChattingNft openChattingNft = new OpenChattingNft(openChatting.getId(), dropId, contractAddress, creatorId);
        openChattingNftRepository.save(openChattingNft);
        OpenChattingMember openChattingMember = new OpenChattingMember(openChatting.getId(), klipUser.getKlipAccountId(), klipUser.getKakaoUserId(), 0L, "test-name", "image-url", Role.NFT_HOLDER);
        openChattingMemberRepository.save(openChattingMember);

        // mock
        when(tokenService.getNftToken(contractAddress, tokenId)).thenReturn(nftToken);

        // when
        OpenChattingStatus status = openChattingService.getOpenChattingStatus(contractAddress, tokenId, klaytnAddress, klipUser);

        // then
        assertThat(status).isEqualTo(new OpenChattingStatus(true, openChattingUrl, true));
    }
}

package com.klipwallet.membership.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.klipwallet.membership.dto.openchatting.OpenChattingStatus;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingNft;
import com.klipwallet.membership.entity.TokenId;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;
import com.klipwallet.membership.repository.OpenChattingMemberRepository;
import com.klipwallet.membership.repository.OpenChattingNftRepository;
import com.klipwallet.membership.repository.OpenChattingRepository;

import static org.assertj.core.api.Assertions.assertThat;

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
    void getOpenChattingStatus_OpenChattingNotFound() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        TokenId tokenId = new TokenId("12341222222");
        // when
        OpenChattingStatus status = openChattingService.getOpenChattingStatus(contractAddress, tokenId);

        // then
        assertThat(status).isEqualTo(new OpenChattingStatus(false, ""));
    }

    @Test
    void getOpenChattingStatus_Success() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        Long dropId = 123123412L;
        TokenId tokenId = new TokenId("1231234123134");
        MemberId creatorId = new MemberId(1);
        String openChattingUrl = "https://open.kakao.com/o/gvJWpNBf";

        OpenChatting openChatting = new OpenChatting("title", "image", "", new KakaoOpenlinkSummary(2L, openChattingUrl), contractAddress, creatorId);
        openChatting = openChattingRepository.save(openChatting);

        OpenChattingNft openChattingNft = new OpenChattingNft(openChatting.getId(), dropId, contractAddress, creatorId);
        openChattingNftRepository.save(openChattingNft);

        // when
        OpenChattingStatus status = openChattingService.getOpenChattingStatus(contractAddress, tokenId);

        // then
        assertThat(status).isEqualTo(new OpenChattingStatus(true, openChattingUrl));
    }
}

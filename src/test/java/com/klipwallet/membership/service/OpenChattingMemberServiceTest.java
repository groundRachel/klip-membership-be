package com.klipwallet.membership.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.klipwallet.membership.adaptor.klip.KlipAccount;
import com.klipwallet.membership.dto.openchatting.OpenChattingMemberCreate;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.KlipUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChattingMember;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;
import com.klipwallet.membership.exception.InvalidRequestException;
import com.klipwallet.membership.repository.OpenChattingMemberRepository;
import com.klipwallet.membership.repository.OpenChattingRepository;
import com.klipwallet.membership.service.kakao.KakaoService;

import static com.klipwallet.membership.entity.OpenChattingMember.Role.NFT_HOLDER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenChattingMemberServiceTest {
    @Autowired
    OpenChattingMemberService openChattingMemberService;

    @MockBean
    KakaoService kakaoService;
    @Autowired
    OpenChattingMemberRepository openChattingMemberRepository;
    @Autowired
    OpenChattingRepository openChattingRepository;

    @AfterEach
    void afterEach() {
        openChattingMemberRepository.deleteAll();
        openChattingMemberRepository.flush();
        openChattingRepository.deleteAll();
        openChattingRepository.flush();
    }

    @Test
    void createMember_memberExist() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        KlipUser klipUser = new KlipAccount(1L, "123-1234", "test@gmail.com", "010-1111-1111");
        KakaoOpenlinkSummary kakaoOpenlinkSummary = new KakaoOpenlinkSummary(1L, "url");
        OpenChatting openChatting = new OpenChatting("title", "image", kakaoOpenlinkSummary, contractAddress, new MemberId(1));
        openChattingRepository.save(openChatting);
        OpenChattingMemberCreate openChattingMemberCreate = new OpenChattingMemberCreate("", "nick", "imageUrl");
        OpenChattingMember openChattingMember =
                new OpenChattingMember(openChatting.getId(), klipUser.getKlipAccountId(), klipUser.getKakaoUserId(), 0L, "test-nick", "imageUrl",
                                       NFT_HOLDER);
        OpenChattingMember member = openChattingMemberRepository.save(openChattingMember);
        when(kakaoService.joinOpenChatting(openChatting, openChattingMember)).thenReturn(new KakaoOpenlinkSummary(1L, "url"));

        // when
        OpenChattingMember openChattingMember1 = openChattingMemberService.createMember(openChatting, openChattingMemberCreate, klipUser);
        assertThat(openChattingMember1).isEqualTo(member);
    }

    @Test
    void createMember_invalidRequest() {
        // given
        Address contractAddress = new Address("0x60ad57f39b235640df83e434caab2dfa6a62838b");
        KlipUser klipUser = new KlipAccount(1L, "123-1234", "test@gmail.com", "010-1111-1111");
        KakaoOpenlinkSummary kakaoOpenlinkSummary = new KakaoOpenlinkSummary(1L, "url");
        OpenChatting openChatting = new OpenChatting("title", "image", kakaoOpenlinkSummary, contractAddress, new MemberId(1));
        OpenChattingMemberCreate openChattingMemberCreate = new OpenChattingMemberCreate("", "", "");
        OpenChattingMember openChattingMember =
                new OpenChattingMember(openChatting.getId(), klipUser.getKlipAccountId(), klipUser.getKakaoUserId(), 0L, "test-nick", "imageUrl",
                                       NFT_HOLDER);

        // when
        try {
            openChattingMemberService.createMember(openChatting, openChattingMemberCreate, klipUser);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(InvalidRequestException.class);
        }
    }
}

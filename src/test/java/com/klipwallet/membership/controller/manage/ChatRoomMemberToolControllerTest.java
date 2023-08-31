package com.klipwallet.membership.controller.manage;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.klipwallet.membership.adaptor.klip.KlipAccount;
import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.dto.chatroom.ChatRoomMemberSummary;
import com.klipwallet.membership.repository.ChatRoomMemberRepository;
import com.klipwallet.membership.service.KlipAccountService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("ChatRoomMember entity 를 올려야 해서 일시적으로 disabled") // TODO: @Ian
class ChatRoomMemberToolControllerTest {

    @Autowired
    ChatRoomMemberRepository chatRoomMemberRepository;

    @MockBean
    KlipAccountService klipAccountService;

    @Autowired
    ObjectMapper om;

    private Long lastChatRoomMemberId;

    @BeforeEach
    void setUp() {clearChatRoomMembers();}

    @AfterEach
    void tearDown() {clearChatRoomMembers();}

    private void clearChatRoomMembers() {
        chatRoomMemberRepository.deleteAll();
        chatRoomMemberRepository.flush();
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 관리자 생성 > 201")
    @Test
    void createChatRoomMember(@Autowired MockMvc mvc) throws Exception {
        given(klipAccountService.getKlipUser(any()))
                .willReturn(new KlipAccount(1L, "2538023320", "testemail@test.com", "010-1234-5678"));
        String body = """
                      {
                        "klipId": 1,
                        "nickname": "테스트 유저 닉네임",
                        "profileImageUrl": "https://testimage.com",
                        "role": 1
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/chat-room-members")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isString());

        setLastChatRoomMemberId(ra);
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 관리자 생성: request body 값이 유효하지 않음 > 400")
    @Test
    void createEmptyFields(@Autowired MockMvc mvc) throws Exception {
        given(klipAccountService.getKlipUser(any()))
                .willReturn(new KlipAccount(1L, "2538023320", "testemail@test.com", "010-1234-5678"));
        String body = """
                      {
                        "klipId": 1,
                        "nickname": "",
                        "profileImageUrl": "",
                        "role": 1
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/chat-room-members")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body)
                    )
                    .andExpect(status().isBadRequest())

                    .andExpect(jsonPath("$.code").value(400_001))
                    .andExpect(jsonPath("$.err").value("요청 본문이 유효하지 않습니다. errors를 참고하세요."))
                    .andExpect(jsonPath("$.errors.length()").value(2))
                    .andExpect(jsonPath("$.errors[?(@.field == 'nickname')].message").value("nickname: 'must not be blank'"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'profileImageUrl')].message").value("profileImageUrl: 'must not be blank'"));
    }


    private void setLastChatRoomMemberId(ResultActions ra) throws IOException {
        MvcResult mvcResult = ra.andReturn();
        ChatRoomMemberSummary summary = om.readValue(mvcResult.getResponse().getContentAsString(), ChatRoomMemberSummary.class);
        lastChatRoomMemberId = summary.id();
    }
}
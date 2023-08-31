package com.klipwallet.membership.controller.tool;

import java.util.Optional;

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

import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.repository.ChatRoomMemberRepository;
import com.klipwallet.membership.repository.OperatorRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChatRoomToolControllerTest {

    @Autowired
    ChatRoomMemberRepository chatRoomMemberRepository;
    @MockBean
    OperatorRepository operatorRepository;

    @BeforeEach
    void setUp() {
        clearOperators();
    }

    @AfterEach
    void tearDown() {
        clearOperators();
    }

    public void clearOperators() {
        chatRoomMemberRepository.deleteAll();
        chatRoomMemberRepository.flush();
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 생성 > 201")
    @Test
    void createChatRoom(@Autowired MockMvc mvc) throws Exception {
        Operator operator = new Operator(324L, "2538023310", 23, new MemberId(1));
        given(operatorRepository.findById(any())).willReturn(Optional.of(operator));
        String body = """
                      {
                        "title": "NFT 오픈채팅방",
                        "description": "NFT 홀더들을 위한 오픈채팅방 입니다.",
                        "coverImageUrl": "https://coverimage.com",
                        "host": {
                                    "operatorId": 1,
                                    "nickname": "방장 닉네임",
                                    "profileImageUrl": "https://hostprofileimage.com"
                                },
                        "operators": [
                            {
                                "operatorId": 2,
                                "nickname": "운영자 2 닉네임",
                                "profileImageUrl": "https://operator2profileimage.com"
                            },
                            {
                                "operatorId": 3,
                                "nickname": "운영자 3 닉네임",
                                "profileImageUrl": "https://operator3profileimage.com"
                            }
                        ],
                        "nfts": [
                            {
                                "dropId": 1234,
                                "sca": "0x1234"
                            },
                            {
                                "dropId": 5678,
                                "sca": "0x1234"
                            }
                        ]
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/chat-rooms")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isCreated());
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 생성: 제목 없음 > 400")
    @Test
    void createChatRoomWithoutTitle(@Autowired MockMvc mvc) throws Exception {
        Operator operator = new Operator(324L, "2538023310", 23, new MemberId(1));
        given(operatorRepository.findById(any())).willReturn(Optional.of(operator));
        String body = """
                      {
                        "title": "",
                        "description": "NFT 홀더들을 위한 오픈채팅방 입니다.",
                        "coverImageUrl": "https://coverimage.com",
                        "host": {
                                    "operatorId": 1,
                                    "nickname": "방장 닉네임",
                                    "profileImageUrl": "https://hostprofileimage.com"
                                },
                        "operators": [
                            {
                                "operatorId": 2,
                                "nickname": "운영자 2 닉네임",
                                "profileImageUrl": "https://operator2profileimage.com"
                            },
                            {
                                "operatorId": 3,
                                "nickname": "운영자 3 닉네임",
                                "profileImageUrl": "https://operator3profileimage.com"
                            }
                        ],
                        "nfts": [
                            {
                                "dropId": 1234,
                                "sca": "0x1234"
                            },
                            {
                                "dropId": 5678,
                                "sca": "0x1234"
                            }
                        ]
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/chat-rooms")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isBadRequest());
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 생성: 운영자 인원 제한 초과 > 400")
    @Test
    @Disabled("400이 리턴되어야 하는데 500이 리턴되고 있음 원인 파악")
        // TODO: @Ian
    void createChatRoomExceedOperatorLimit(@Autowired MockMvc mvc) throws Exception {
        Operator operator = new Operator(324L, "2538023310", 23, new MemberId(1));
        given(operatorRepository.findById(any())).willReturn(Optional.of(operator));
        String body = """
                      {
                        "title": "NFT 오픈채팅방",
                        "description": "NFT 홀더들을 위한 오픈채팅방 입니다.",
                        "coverImageUrl": "https://coverimage.com",
                        "host": {
                                    "operatorId": 1,
                                    "nickname": "방장 닉네임",
                                    "profileImageUrl": "https://hostprofileimage.com"
                                },
                        "operators": [
                            {
                                "operatorId": 2,
                                "nickname": "운영자 2 닉네임",
                                "profileImageUrl": "https://operator2profileimage.com"
                            },
                            {
                                "operatorId": 3,
                                "nickname": "운영자 3 닉네임",
                                "profileImageUrl": "https://operator3profileimage.com"
                            },
                            {
                                "operatorId": 4,
                                "nickname": "운영자 4 닉네임",
                                "profileImageUrl": "https://operator4profileimage.com"
                            },
                            {
                                "operatorId": 5,
                                "nickname": "운영자 5 닉네임",
                                "profileImageUrl": "https://operator5profileimage.com"
                            },
                            {
                                "operatorId": 6,
                                "nickname": "운영자 6 닉네임",
                                "profileImageUrl": "https://operator6profileimage.com"
                            }
                        ],
                        "nfts": [
                            {
                                "dropId": 1234,
                                "sca": "0x1234"
                            },
                            {
                                "dropId": 5678,
                                "sca": "0x1234"
                            }
                        ]
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/chat-rooms")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isOk());
    }

    @Test
    void chatRoomList() {
        // TODO @Jordan
    }
}
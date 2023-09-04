package com.klipwallet.membership.controller.tool;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.repository.ChatRoomMemberRepository;
import com.klipwallet.membership.service.OperatorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:kakao-api-test.properties")
@Slf4j
class ChatRoomToolControllerTest {

    @Autowired
    ChatRoomMemberRepository chatRoomMemberRepository;
    @MockBean
    OperatorService operatorService;
    @Value("${user-id}")
    private String kakaoUserId;

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
    @Disabled("실제 오픈채팅방 생성되어 Disabled")
    void createChatRoom(@Autowired MockMvc mvc) throws Exception {
        Operator operator = new Operator(324L, kakaoUserId, 23, new MemberId(1));
        given(operatorService.tryGetOperator(any())).willReturn(operator);
        String body = """
                      {
                        "title": "NFT 오픈채팅방",
                        "description": "NFT 홀더들을 위한 오픈채팅방 입니다.",
                        "coverImageUrl": "https://membership.dev.klipwallet.com/klip-membership/test.jpg",
                        "host": {
                                    "operatorId": 1,
                                    "nickname": "방장 닉네임",
                                    "profileImageUrl": "https://membership.dev.klipwallet.com/klip-membership/test.jpg"
                                },
                        "operators": [
                            {
                                "operatorId": 2,
                                "nickname": "운영자 2 닉네임",
                                "profileImageUrl": "https://membership.dev.klipwallet.com/klip-membership/test.jpg"
                            },
                            {
                                "operatorId": 3,
                                "nickname": "운영자 3 닉네임",
                                "profileImageUrl": "https://membership.dev.klipwallet.com/klip-membership/test.jpg"
                            }
                        ],
                        "nfts": [
                            {
                                "dropId": 39700080005,
                                "sca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            },
                            {
                                "dropId": 39700080005,
                                "sca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            }
                        ]
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/chat-rooms")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isString())
                    .andExpect(jsonPath("$.openChatRoomId").isString())
                    .andExpect(jsonPath("$.openChatRoomUrl").isString())
                    .andExpect(jsonPath("$.title").value("NFT 오픈채팅방"));
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 생성: 제목 없음 > 400")
    @Test
    void createChatRoomCheckNull(@Autowired MockMvc mvc) throws Exception {
        Operator operator = new Operator(324L, kakaoUserId, 23, new MemberId(1));
        given(operatorService.tryGetOperator(any())).willReturn(operator);
        String body = """
                      {
                        "title": "",
                        "description": "NFT 홀더들을 위한 오픈채팅방 입니다.",
                        "coverImageUrl": "https://coverimage.com",
                        "host": {
                                    "operatorId": 1,
                                    "nickname": "",
                                    "profileImageUrl": ""
                                },
                        "operators": [
                            {
                                "operatorId": 2,
                                "nickname": "",
                                "profileImageUrl": ""
                            },
                            {
                                "operatorId": 3,
                                "nickname": "운영자 3 닉네임",
                                "profileImageUrl": "https://operator3profileimage.com"
                            }
                        ],
                        "nfts": [
                            {
                                "dropId": null,
                                "sca": null
                            },
                            {
                                "dropId": 39700080005,
                                "sca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            }
                        ]
                      }
                      """;

        var ra = mvc.perform(post("/tool/v1/chat-rooms")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body).locale(Locale.KOREA))
                    .andExpect(status().isBadRequest())

                    .andExpect(jsonPath("$.code").value(400_001))
                    .andExpect(jsonPath("$.err").value("요청 본문이 유효하지 않습니다. errors를 참고하세요."))
                    .andExpect(jsonPath("$.errors.length()").value(7))
                    .andExpect(jsonPath("$.errors[?(@.field == 'title')].message").value("title: '공백일 수 없습니다'"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'host.nickname')].message").value("host.nickname: '공백일 수 없습니다'"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'host.profileImageUrl')].message").value("host.profileImageUrl: '공백일 수 없습니다'"))
                    .andExpect(
                            jsonPath("$.errors[?(@.field == 'operators[0].nickname')].message").value(
                                    "operators[0].nickname: '공백일 수 없습니다'"))
                    .andExpect(
                            jsonPath("$.errors[?(@.field == 'operators[0].profileImageUrl')].message").value(
                                    "operators[0].profileImageUrl: '공백일 수 없습니다'"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'nfts[0].dropId')].message").value("nfts[0].dropId: '널이어서는 안됩니다'"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'nfts[0].sca')].message").value("nfts[0].sca: '널이어서는 안됩니다'"));

    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 생성: 운영자 인원 제한 초과 > 400")
    @Test
    void createChatRoomExceedOperatorLimit(@Autowired MockMvc mvc) throws Exception {
        Operator operator = new Operator(324L, kakaoUserId, 23, new MemberId(1));
        given(operatorService.tryGetOperator(any())).willReturn(operator);
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
                                "dropId": 39700080005,
                                "sca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            },
                            {
                                "dropId": 39700080005,
                                "sca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            }
                        ]
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/chat-rooms")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void chatRoomList() {
        // TODO @Jordan
    }
}
package com.klipwallet.membership.controller.tool;

import java.util.List;
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
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.OpenChatting;
import com.klipwallet.membership.entity.OpenChatting.Status;
import com.klipwallet.membership.entity.Operator;
import com.klipwallet.membership.entity.kakao.KakaoOpenlinkSummary;
import com.klipwallet.membership.repository.OpenChattingMemberRepository;
import com.klipwallet.membership.repository.OpenChattingRepository;
import com.klipwallet.membership.service.OperatorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:kakao-api-test.properties")
@Slf4j
class OpenChattingToolControllerTest {

    @Autowired
    OpenChattingMemberRepository openChattingMemberRepository;
    @Autowired
    OpenChattingRepository openChattingRepository;
    @MockBean
    OperatorService operatorService;
    @Value("${user-id}")
    private String kakaoUserId;
    @Value("${participant-id}")
    private String kakaoPartnerId;

    @BeforeEach
    void setUp() {
        clearOperators();
    }

    @AfterEach
    void tearDown() {
        clearOperators();
    }

    public void clearOperators() {
        openChattingMemberRepository.deleteAll();
        openChattingMemberRepository.flush();
        openChattingRepository.deleteAll();
        openChattingRepository.flush();
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 생성 > 201")
    @Test
    @Disabled("실제 오픈채팅방 생성되어 Disabled")
    void createOpenChatting(@Autowired MockMvc mvc) throws Exception {
        Operator host = new Operator(324L, kakaoUserId, new MemberId(23));
        given(operatorService.tryGetOperator(1L)).willReturn(host);
        Operator operator = new Operator(325L, kakaoPartnerId, new MemberId(23));
        given(operatorService.tryGetOperator(2L)).willReturn(operator);
        String body = """
                      {
                        "title": "NFT 오픈채팅방",
                        "description": "NFT 홀더들을 위한 오픈채팅방 입니다.",
                        "coverImageUrl": "https://klip-media.dev.klaytn.com/klip-membership/test.jpg",
                        "host": {
                                    "operatorId": 1,
                                    "nickname": "방장 닉네임",
                                    "profileImageUrl": "https://klip-media.dev.klaytn.com/klip-membership/test.jpg"
                                },
                        "operators": [
                            {
                                "operatorId": 2,
                                "nickname": "운영자 2 닉네임",
                                "profileImageUrl": "https://klip-media.dev.klaytn.com/klip-membership/test.jpg"
                            }
                        ],
                        "nfts": [
                            {
                                "dropId": 39700080005,
                                "klipDropsSca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            },
                            {
                                "dropId": 39700080005,
                                "klipDropsSca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            }
                        ]
                      }
                      """;

        var ra = mvc.perform(post("/tool/v1/openchattings")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isString())
                    .andExpect(jsonPath("$.openChattingId").isString())
                    .andExpect(jsonPath("$.openChattingUrl").isString())
                    .andExpect(jsonPath("$.title").value("NFT 오픈채팅방"));
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 생성: 제목 없음 > 400")
    @Test
    void createOpenChattingCheckNull(@Autowired MockMvc mvc) throws Exception {
        Operator operator = new Operator(324L, "2238023120", new MemberId(23));
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
                                "klipDropsSca": null
                            },
                            {
                                "dropId": 39700080005,
                                "klipDropsSca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            }
                        ]
                      }
                      """;

        var ra = mvc.perform(post("/tool/v1/openchattings")
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
                    .andExpect(jsonPath("$.errors[?(@.field == 'nfts[0].klipDropsSca')].message").value("nfts[0].klipDropsSca: '널이어서는 안됩니다'"));

    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 생성: 운영자 인원 제한 초과 > 400")
    @Test
    void createOpenChattingExceedOperatorLimit(@Autowired MockMvc mvc) throws Exception {
        Operator operator = new Operator(324L, "2238023120", new MemberId(23));
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
                                "klipDropsSca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            },
                            {
                                "dropId": 39700080005,
                                "klipDropsSca": "0xa9A95C5feF43830D5d67156a2582A2E793aCb465"
                            }
                        ]
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/openchattings")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isBadRequest());
    }

    @WithPartnerUser
    @DisplayName("오픈채팅방 목록 조회 > 200")
    @Test
    void openChattingList(@Autowired MockMvc mvc) throws Exception {
        createSampleOpenChattings();
        mvc.perform(get("/tool/v1/openchattings"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalElements").value(11L))
           .andExpect(jsonPath("$.totalPages").value(1))
           .andExpect(jsonPath("$.numberOfElements").value(11))
           .andExpect(jsonPath("$.content.length()").value(11L))
           .andExpect(jsonPath("$.content[0].id").isNotEmpty())
           .andExpect(jsonPath("$.content[0].title").value("t11"))
           .andExpect(jsonPath("$.content[0].openChattingId").value("303890410"))
           .andExpect(jsonPath("$.content[0].openChattingUrl").value("https://open.kakao.com/o/gIRPLPDda"));
        mvc.perform(get("/tool/v1/openchattings").param("status", Status.ACTIVATED.toDisplay()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalElements").value(8L))
           .andExpect(jsonPath("$.totalPages").value(1))
           .andExpect(jsonPath("$.numberOfElements").value(8))
           .andExpect(jsonPath("$.content[1].status").value(Status.ACTIVATED.toDisplay()));
        mvc.perform(get("/tool/v1/openchattings").param("status", Status.DELETED.toDisplay()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalElements").value(3L))
           .andExpect(jsonPath("$.totalPages").value(1))
           .andExpect(jsonPath("$.numberOfElements").value(3))
           .andExpect(jsonPath("$.content[0].status").value(Status.DELETED.toDisplay()));
    }

    private void createSampleOpenChattings() {
        List<OpenChatting> openChattings = List.of(
                new OpenChatting("t1", "https://cover1.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(1)),
                new OpenChatting("t2", "https://cover2.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(2)),
                new OpenChatting("t3", "https://cover3.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(3)),
                new OpenChatting("t4", "https://cover4.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(4)),
                new OpenChatting("t5", "https://cover5.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(5)),
                new OpenChatting("t6", "https://cover6.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(6)),
                new OpenChatting("t7", "https://cover7.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(7)),
                new OpenChatting("t8", "https://cover8.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(8)),
                new OpenChatting("t9", "https://cover9.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(9)),
                new OpenChatting("t10", "https://cover10.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(10)),
                new OpenChatting("t11", "https://cover11.jpg", new KakaoOpenlinkSummary(303890410L, "https://open.kakao.com/o/gIRPLPDda"),
                                 new Address("0xa005e82487fb629923b9598offdrc2e9499focab"), new MemberId(11))
        );
        List<OpenChatting> results = openChattingRepository.saveAll(openChattings);
        results.get(3).deleteBy(new MemberId(3));
        results.get(4).deleteBy(new MemberId(4));
        results.get(10).deleteBy(new MemberId(4));
        openChattingRepository.saveAll(results);
        openChattingRepository.flush();
    }
}
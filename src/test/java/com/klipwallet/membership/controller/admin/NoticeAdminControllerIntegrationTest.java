package com.klipwallet.membership.controller.admin;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.config.security.WithAuthenticatedUser;
import com.klipwallet.membership.dto.notice.NoticeDto.Summary;
import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.repository.NoticeRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NoticeAdminControllerIntegrationTest {
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    ObjectMapper om;
    private Integer lastNoticeId;

    @WithAdminUser(memberId = 24)
    @DisplayName("관리자 공지사항 생성 > 201")
    @Test
    void create(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "title": "클립 멤버십 툴이 공식 오픈하였습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다.</p>"
                      }
                      """;
        var ra = mvc.perform(post("/admin/v1/notices")
                                     .contentType(APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber());
        setLastNoticeId(ra);
    }

    private void setLastNoticeId(ResultActions ra) throws IOException {
        MvcResult mvcResult = ra.andReturn();
        Summary summary = om.readValue(mvcResult.getResponse().getContentAsString(), Summary.class);
        lastNoticeId = summary.id();
    }

    @WithAuthenticatedUser(authorities = "ROLE_PARTNER")
    @DisplayName("관리자 공지사항 생성: 파트너 권한으로 시도 > 403")
    @Test
    void createOnPartner(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "title": "클립 멤버십 툴이 공식 오픈하였습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다.</p>"
                      }
                      """;
        mvc.perform(post("/admin/v1/notices")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isForbidden());
        // TODO @Jordan 적절한 Security 예외 처리
        //           .andExpect(jsonPath("$.code").value(1024))
        //           .andExpect(jsonPath("$.err").value("적절한 오류 메시지"));
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 생성: title, body가 유효하지 않음 > 400")
    @Test
    void createEmptyTitleAndBody(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "title": null,
                        "body": ""
                      }
                      """;
        mvc.perform(post("/admin/v1/notices")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest());
        // TODO @Jordan 적절한 BadRequest 예외 처리(DefaultHandlerExceptionResolver, MethodArgumentNotValidException)
        //           .andExpect(jsonPath("$.code").value(1024))
        //           .andExpect(jsonPath("$.err").value("적절한 오류 메시지"));
    }

    @WithAdminUser(memberId = 24)
    @DisplayName("관리자 공지사항 수정 > 200")
    @Test
    void update(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        String body = """
                      {
                        "title": "클립 멤버십 툴 1.1.0이 릴리즈 되었습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>"
                      }
                      """;
        mvc.perform(put("/admin/v1/notices/{0}", noticeId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(noticeId))
           .andExpect(jsonPath("$.title").value("클립 멤버십 툴 1.1.0이 릴리즈 되었습니다."))
           .andExpect(jsonPath("$.body").value("<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>"))
           .andExpect(jsonPath("$.main").value(false))
           .andExpect(jsonPath("$.createdAt").isNotEmpty())
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.creator.id").value(24))
           .andExpect(jsonPath("$.creator.name").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(24))
           .andExpect(jsonPath("$.updater.name").isNotEmpty());
    }

    @WithAdminUser(memberId = 25)
    @DisplayName("관리자 공지사항 수정: 메인 공지 설정 > 200")
    @Test
    void updateWithActivatedMain(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        updateWithActivatedMain(mvc, noticeId);
    }

    private void updateWithActivatedMain(MockMvc mvc, Integer noticeId) throws Exception {
        String body = """
                      {
                        "title": "클립 멤버십 툴 1.1.0이 릴리즈 되었습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>",
                        "main": true
                      }
                      """;
        mvc.perform(put("/admin/v1/notices/{0}", noticeId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(noticeId))
           .andExpect(jsonPath("$.title").value("클립 멤버십 툴 1.1.0이 릴리즈 되었습니다."))
           .andExpect(jsonPath("$.body").value("<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>"))
           .andExpect(jsonPath("$.main").value(true))
           .andExpect(jsonPath("$.createdAt").isNotEmpty())
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.creator.id").value(25))
           .andExpect(jsonPath("$.creator.name").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(25))
           .andExpect(jsonPath("$.updater.name").isNotEmpty());
    }

    @WithAdminUser(memberId = 25)
    @DisplayName("관리자 공지사항 수정: 메인 공지 2번 연속 설정 > 200")
    @Test
    void updateTwiceWithMain(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId1 = lastNoticeId;
        create(mvc);
        Integer noticeId2 = lastNoticeId;
        updateWithActivatedMain(mvc, noticeId1);
        updateWithActivatedMain(mvc, noticeId2);
        // noticeId1.main=false (noticeId2.main=true가 되므로 이전 메인 노출이었던 noticeId1의 main 값은 false가 되어야함)
        assertMainDeactivated(noticeId1);
    }

    private void assertMainDeactivated(Integer noticeId1) {
        Notice notice2 = noticeRepository.findById(noticeId1).orElse(null);
        assertThat(notice2).isNotNull();
        assertThat(notice2.isMain()).isFalse();
    }

    @Disabled("/oauth2/authorization/google 으로 redirect 되고 있어서 수정이 요구됨.")
    @DisplayName("관리자 공지사항 수정: 인증되지 않음으로 시도 > 401")
    @Test
    void updateOnNoAuth(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        String body = """
                      {
                        "title": "클립 멤버십 툴 1.1.0이 릴리즈 되었습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>"
                      }
                      """;
        mvc.perform(put("/admin/v1/notices/{0}", noticeId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isForbidden());
        // TODO @Jordan 적절한 AuthenticationException 처리 (redirection이 아닌 401)
        //           .andExpect(jsonPath("$.code").value(1024))
        //           .andExpect(jsonPath("$.err").value("적절한 오류 메시지"));
    }

    @WithAuthenticatedUser(authorities = "ROLE_ADMIN")
    @DisplayName("관리자 공지사항 수정: 존재하지 않는 공지사항 수정 시도 > 404")
    @Test
    void updateNotExistsNotice(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = -2;
        String body = """
                      {
                        "title": "클립 멤버십 툴 1.1.0이 릴리즈 되었습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>"
                      }
                      """;
        mvc.perform(put("/admin/v1/notices/{0}", noticeId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value("error.notice.not-found.1"))
           .andExpect(jsonPath("$.err").value("공지사항을 찾을 수 없습니다. ID: %d".formatted(noticeId)));
    }
}
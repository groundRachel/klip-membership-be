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
import com.klipwallet.membership.dto.faq.FaqSummary;
import com.klipwallet.membership.entity.Faq.Status;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.repository.FaqRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FaqAdminControllerIntegrationTest {
    @Autowired
    FaqRepository faqRepository;
    @Autowired
    ObjectMapper om;
    private Integer lastNoticeId;

    @WithAdminUser(memberId = 24)
    @DisplayName("관리자 Faq 생성 > 201")
    @Test
    void create(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "title": "멤버십 툴에 어떻게 가입하나요?",
                        "body": "<p>GX 파트너는 누구나 가입할 수 있습니다.</p>"
                      }
                      """;
        var ra = mvc.perform(post("/admin/v1/faqs")
                                     .contentType(APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber());
        setLastNoticeId(ra);
    }

    private void setLastNoticeId(ResultActions ra) throws IOException {
        MvcResult mvcResult = ra.andReturn();
        FaqSummary summary = om.readValue(mvcResult.getResponse().getContentAsString(), FaqSummary.class);
        lastNoticeId = summary.id();
    }

    @WithAuthenticatedUser(authorities = "ROLE_PARTNER")
    @DisplayName("관리자 FAQ 생성: 파트너 권한으로 시도 > 403")
    @Test
    void createOnPartner(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "title": "멤버십 툴에 어떻게 가입하나요?",
                        "body": "<p>GX 파트너는 누구나 가입할 수 있습니다.</p>"
                      }
                      """;
        mvc.perform(post("/admin/v1/faqs")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isForbidden());
        // TODO @Jordan 적절한 Security 예외 처리
        //           .andExpect(jsonPath("$.code").value(1024))
        //           .andExpect(jsonPath("$.err").value("적절한 오류 메시지"));
    }

    @WithAdminUser
    @DisplayName("관리자 FAQ 생성: title, body가 유효하지 않음 > 400")
    @Test
    void createEmptyTitleAndBody(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "title": null,
                        "body": ""
                      }
                      """;
        mvc.perform(post("/admin/v1/faqs")
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest());
        // TODO @Jordan 적절한 BadRequest 예외 처리(DefaultHandlerExceptionResolver, MethodArgumentNotValidException)
        //           .andExpect(jsonPath("$.code").value(1024))
        //           .andExpect(jsonPath("$.err").value("적절한 오류 메시지"));
    }

    @WithAdminUser(memberId = 24)
    @DisplayName("관리자 FAQ 수정 > 200")
    @Test
    void update(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer faqId = lastNoticeId;
        String body = """
                      {
                        "title": "멤버십 툴은 어떻게 사용하나요?",
                        "body": "<p>아래 링크를 통해 확인하실 수 있습니다.</p>",
                        "status": "active"
                      }
                      """;
        mvc.perform(put("/admin/v1/faqs/{0}", faqId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(faqId))
           .andExpect(jsonPath("$.title").value("멤버십 툴은 어떻게 사용하나요?"))
           .andExpect(jsonPath("$.body").value("<p>아래 링크를 통해 확인하실 수 있습니다.</p>"))
           .andExpect(jsonPath("$.status").value(Status.ACTIVE.toDisplay()))
           .andExpect(jsonPath("$.createdAt").isNotEmpty())
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.creator.id").value(24))
           .andExpect(jsonPath("$.creator.name").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(24))
           .andExpect(jsonPath("$.updater.name").isNotEmpty());
    }

    @WithAdminUser(memberId = 24)
    @DisplayName("관리자 FAQ 수정: body, status가 유효하지 않음 > 400")
    @Test
    void updateEmptyBodyAndStatus(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer faqId = lastNoticeId;
        String body = """
                      {
                        "title": "멤버십 툴은 어떻게 사용하나요?",
                        "body": "",
                        "status": ""
                      }
                      """;
        mvc.perform(put("/admin/v1/faqs/{0}", faqId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest());
        // TODO @Jordan 적절한 BadRequest 예외 처리(DefaultHandlerExceptionResolver, MethodArgumentNotValidException)
        //           .andExpect(jsonPath("$.code").value(1024))
        //           .andExpect(jsonPath("$.err").value("적절한 오류 메시지"));
    }

    @Disabled("/oauth2/authorization/google 으로 redirect 되고 있어서 수정이 요구됨.")
    @DisplayName("관리자 FAQ 수정: 인증되지 않음으로 시도 > 401")
    @Test
    void updateOnNoAuth(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer faqId = lastNoticeId;
        String body = """
                      {
                        "title": "클립 멤버십 툴 1.1.0이 릴리즈 되었습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>"
                      }
                      """;
        mvc.perform(put("/admin/v1/faqs/{0}", faqId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isForbidden());
        // TODO @Jordan 적절한 AuthenticationException 처리 (redirection이 아닌 401)
        //           .andExpect(jsonPath("$.code").value(1024))
        //           .andExpect(jsonPath("$.err").value("적절한 오류 메시지"));
    }

    @WithAuthenticatedUser(authorities = "ROLE_ADMIN")
    @DisplayName("관리자 FAQ 수정: 존재하지 않는 FAQ 수정 시도 > 404")
    @Test
    void updateNotExistsFAQ(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer faqId = -2;
        String body = """
                      {
                        "title": "클립 멤버십 툴 1.1.0이 릴리즈 되었습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>"
                      }
                      """;
        mvc.perform(put("/admin/v1/faqs/{0}", faqId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(ErrorCode.FAQ_NOT_FOUND.getCode()))
           .andExpect(jsonPath("$.err").value("FAQ를 찾을 수 없습니다. ID: %d".formatted(faqId)));
    }

}

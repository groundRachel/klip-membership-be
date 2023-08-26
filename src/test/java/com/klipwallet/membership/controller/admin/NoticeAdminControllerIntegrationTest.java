package com.klipwallet.membership.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.dto.notice.NoticeDto.Summary;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.entity.Notice.Status;
import com.klipwallet.membership.repository.NoticeRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NoticeAdminControllerIntegrationTest {
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    ObjectMapper om;
    private Integer lastNoticeId;

    @BeforeEach
    void setUp() {
        clearNotices();
    }

    @AfterEach
    void tearDown() {
        clearNotices();
    }

    private void clearNotices() {
        noticeRepository.deleteAll();
        noticeRepository.flush();
    }

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

    @DisplayName("관리자 공지사항 생성: 인증 없음. > 401")
    @Test
    void createOnUnauthenticated(@Autowired MockMvc mvc) throws Exception {
        String body = """
                      {
                        "title": "클립 멤버십 툴이 공식 오픈하였습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다.</p>"
                      }
                      """;
        mvc.perform(post("/admin/v1/notices")
                            .contentType(ALL)
                            .content(body))
           .andExpect(status().isFound());  // 302
        // FIXME @Jordan 임시로 개발 편의성을 위해 302 처리 중 추후 아래 401로 변경할 예정
        //           .andExpect(status().isUnauthorized())
        //           .andExpect(jsonPath("$.code").value(401_000))
        //           .andExpect(jsonPath("$.err").value("인증되지 않았습니다."));
    }

    @WithPartnerUser
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
                            .contentType(ALL)
                            .content(body))
           .andExpect(status().isForbidden())
           .andExpect(forwardedUrl("/error/403"))   // forward 된 후 응답 모델로 비교하고 싶었으나, 이게 최선임
           .andExpect(request().attribute(WebAttributes.ACCESS_DENIED_403, instanceOf(AccessDeniedException.class)));
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
                            .content(body)
                            .locale(Locale.KOREAN))
           .andExpect(status().isBadRequest())

           .andExpect(jsonPath("$.code").value(400_001))
           .andExpect(jsonPath("$.err").value("요청 본문이 유효하지 않습니다. errors를 참고하세요."))
           .andExpect(jsonPath("$.errors.length()").value(2))
           .andExpect(jsonPath("$.errors[?(@.field == 'title')].message").value("title: '공백일 수 없습니다'"))
           .andExpect(jsonPath("$.errors[?(@.field == 'body')].message").value("body: '공백일 수 없습니다'"));
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 draft 목록 조회 > 200")
    @Test
    void draftList(@Autowired MockMvc mvc) throws Exception {
        createSampleNotices();

        mvc.perform(get("/admin/v1/notices")
                            .param("status", Status.DRAFT.toDisplay()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(3L))
           .andExpect(jsonPath("$[0].id").isNotEmpty())
           .andExpect(jsonPath("$[0].title").value("[안내] App2App Javascript SDK 신규 버전 배포 안내 - 2.2.1"))
           .andExpect(jsonPath("$[0].primary").value(false))
           .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
           .andExpect(jsonPath("$[0].creator.id").value(1))
           .andExpect(jsonPath("$[0].creator.name").isNotEmpty())
           .andExpect(jsonPath("$[0].updatedAt").isNotEmpty())
           .andExpect(jsonPath("$[0].updater.id").value(1))
           .andExpect(jsonPath("$[0].updater.name").isNotEmpty());
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 live 목록 조회 > 200")
    @Test
    void liveList(@Autowired MockMvc mvc) throws Exception {
        createSampleNotices();

        mvc.perform(get("/admin/v1/notices")
                            .param("status", Status.LIVE.toDisplay()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(4L))
           .andExpect(jsonPath("$[0].id").isNotEmpty())
           .andExpect(jsonPath("$[0].title").value("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내"))
           .andExpect(jsonPath("$[0].primary").value(false))
           .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
           .andExpect(jsonPath("$[0].creator.id").value(1))
           .andExpect(jsonPath("$[0].creator.name").isNotEmpty())
           .andExpect(jsonPath("$[0].updatedAt").isNotEmpty())
           .andExpect(jsonPath("$[0].updater.id").value(4))
           .andExpect(jsonPath("$[0].updater.name").isNotEmpty());
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 inactive 목록 조회 > 200")
    @Test
    void inactiveList(@Autowired MockMvc mvc) throws Exception {
        createSampleNotices();

        mvc.perform(get("/admin/v1/notices")
                            .param("status", Status.INACTIVE.toDisplay()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(3L))
           .andExpect(jsonPath("$[0].id").isNotEmpty())
           .andExpect(jsonPath("$[0].title").value("[문서 개선] 클립 NFT 메타데이터 표준 안내 페이지 추가"))
           .andExpect(jsonPath("$[0].primary").value(false))
           .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
           .andExpect(jsonPath("$[0].creator.id").value(2))
           .andExpect(jsonPath("$[0].creator.name").isNotEmpty())
           .andExpect(jsonPath("$[0].updatedAt").isNotEmpty())
           .andExpect(jsonPath("$[0].updater.id").value(5))
           .andExpect(jsonPath("$[0].updater.name").isNotEmpty());
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 delete 목록 조회 > 400")
    @Test
    void deleteList(@Autowired MockMvc mvc) throws Exception {
        createSampleNotices();

        mvc.perform(get("/admin/v1/notices")
                            .param("status", Status.DELETE.toDisplay()))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Failed to convert 'status' with value: 'delete'"));
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 목록 조회: invalid status query > 400")
    @Test
    void listInvalidStatus(@Autowired MockMvc mvc) throws Exception {
        createSampleNotices();

        mvc.perform(get("/admin/v1/notices")
                            .param("status", "something"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Required parameter 'status' is not present."));
    }

    /**
     * 0 ~ 2: draft, 3 ~ 6: live, 7 ~ 9: inactive, 10 ~ 11: delete
     */
    private void createSampleNotices() {
        List<Notice> notices = List.of(
                new Notice("[안내] 월렛커넥트 연동 지원 안내", "1", new MemberId(1)),
                new Notice("[안내] App2App 멀티체인 확장 안내 - 폴리곤", "2", new MemberId(2)),
                new Notice("[안내] App2App Javascript SDK 신규 버전 배포 안내 - 2.2.1", "3", new MemberId(1)),
                new Notice("[안내] app2app 클립 호출 가이드 변경 안내", "4", new MemberId(2)),
                new Notice("[안내] Klip app2app API 개선 (예상 가스비 반환) 및 기타 변경 사항 안내", "5", new MemberId(1)),
                new Notice("[안내] Klip app2app API 개선 (이더리움 및 수수료 대납 지원) 및 기타 변경 사항 안내", "6", new MemberId(2)),
                new Notice("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내", "7", new MemberId(1)),
                new Notice("Klip Developer Forum에 게시글을 작성하기 전, 꼭! 확인해주세요.", "8", new MemberId(2)),
                new Notice("[기능 개선] App2App REST API - execute_contract 및 sign_message 기능 추가", "9", new MemberId(1)),
                new Notice("[문서 개선] 클립 NFT 메타데이터 표준 안내 페이지 추가", "10", new MemberId(2)),
                new Notice("INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils", "10", new MemberId(1)),
                new Notice("BeanFactory id=f7ff75ef-e558-3499-bc4a-1f50f170b10d", "11", new MemberId(2)));
        List<Notice> results = noticeRepository.saveAll(notices);

        results.get(3).changeStatus(Status.LIVE, new MemberId(3));
        results.get(4).changeStatus(Status.LIVE, new MemberId(4));
        results.get(5).changeStatus(Status.LIVE, new MemberId(3));
        results.get(6).changeStatus(Status.LIVE, new MemberId(4));

        results.get(7).changeStatus(Status.INACTIVE, new MemberId(5));
        results.get(8).changeStatus(Status.INACTIVE, new MemberId(6));
        results.get(9).changeStatus(Status.INACTIVE, new MemberId(5));

        results.get(10).deleteBy(new MemberId(6));
        results.get(11).deleteBy(new MemberId(5));

        noticeRepository.saveAll(results);
        noticeRepository.flush();
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 상세 조회 > 200")
    @Test
    void detail(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        mvc.perform(get("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(noticeId))
           .andExpect(jsonPath("$.title").value("클립 멤버십 툴이 공식 오픈하였습니다."))
           .andExpect(jsonPath("$.body").value("<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다.</p>"))
           .andExpect(jsonPath("$.primary").value(false))
           .andExpect(jsonPath("$.status").value(Status.DRAFT.toDisplay()))
           .andExpect(jsonPath("$.livedAt").value(nullValue()))
           .andExpect(jsonPath("$.createdAt").isNotEmpty())
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.creator.id").value(23))
           .andExpect(jsonPath("$.creator.name").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(23))
           .andExpect(jsonPath("$.updater.name").isNotEmpty());
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 상세 조회: 공지사항이 존재하지 않음 > 404")
    @Test
    void detailNotExists(@Autowired MockMvc mvc) throws Exception {
        Integer noticeId = -23;
        mvc.perform(get("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(404_001))
           .andExpect(jsonPath("$.err").value("공지사항을 찾을 수 없습니다. ID: %d".formatted(noticeId)));
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 상세 조회: String 형 공지 아이디 > 400")
    @Test
    void detailInvalidNoticeId(@Autowired MockMvc mvc) throws Exception {
        String noticeId = "notice_23";
        mvc.perform(get("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Failed to convert 'noticeId' with value: '%s'".formatted(noticeId)));
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
           .andExpect(jsonPath("$.primary").value(false))
           .andExpect(jsonPath("$.status").value(Status.DRAFT.toDisplay()))
           .andExpect(jsonPath("$.livedAt").value(nullValue()))
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
    void updateWithPrimaryOn(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        updateWithPrimaryOn(mvc, noticeId);
    }

    private void updateWithPrimaryOn(MockMvc mvc, Integer noticeId) throws Exception {
        String body = """
                      {
                        "title": "클립 멤버십 툴 1.1.0이 릴리즈 되었습니다.",
                        "body": "<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>",
                        "primary": true
                      }
                      """;
        mvc.perform(put("/admin/v1/notices/{0}", noticeId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(noticeId))
           .andExpect(jsonPath("$.title").value("클립 멤버십 툴 1.1.0이 릴리즈 되었습니다."))
           .andExpect(jsonPath("$.body").value("<p>클립 멤버십 툴은 NFT 홀더들에게 오픈 채팅 등의 구독 서비스를 제공하는 서비스입니다. KlipDrops에 이이서 KlipPartners 까지 지원합니다.</p>"))
           .andExpect(jsonPath("$.primary").value(true))
           .andExpect(jsonPath("$.status").value(Status.DRAFT.toDisplay()))
           .andExpect(jsonPath("$.livedAt").value(nullValue()))
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
        updateWithPrimaryOn(mvc, noticeId1);
        updateWithPrimaryOn(mvc, noticeId2);
        // noticeId1.main=false (noticeId2.main=true가 되므로 이전 메인 노출이었던 noticeId1의 main 값은 false가 되어야함)
        assertPrimaryOff(noticeId1);
    }

    private void assertPrimaryOff(Integer noticeId1) {
        Notice notice2 = noticeRepository.findById(noticeId1).orElse(null);
        assertThat(notice2).isNotNull();
        assertThat(notice2.isPrimary()).isFalse();
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 수정: 존재하지 않는 공지사항 수정 시도 > 404")
    @Test
    void updateNotExists(@Autowired MockMvc mvc) throws Exception {
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
           .andExpect(jsonPath("$.code").value(404_001))
           .andExpect(jsonPath("$.err").value("공지사항을 찾을 수 없습니다. ID: %d".formatted(noticeId)));
    }

    @WithAdminUser(memberId = 27)
    @DisplayName("관리자 공지사항 상태 변경: draft -> live > 200")
    @Test
    void changeStatusDraftToLive(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        String body = """
                      { "value": "live" }
                      """;
        mvc.perform(put("/admin/v1/notices/{0}/status", noticeId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.value").value(Status.LIVE.toDisplay()));

        // status = live & livedAt exists & change updatedAt/updatedBy
        mvc.perform(get("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(noticeId))
           .andExpect(jsonPath("$.status").value(Status.LIVE.toDisplay()))
           .andExpect(jsonPath("$.livedAt").isNotEmpty())
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(27))
           .andExpect(jsonPath("$.updater.name").isNotEmpty());
    }

    @WithAdminUser(memberId = 27)
    @DisplayName("관리자 공지사항 상태 변경: draft -> live -> inactive > 200")
    @Test
    void changeStatusLiveToInactive(@Autowired MockMvc mvc) throws Exception {
        changeStatusDraftToLive(mvc);
        Integer noticeId = lastNoticeId;
        String body = """
                      { "value": "inactive" }
                      """;
        mvc.perform(put("/admin/v1/notices/{0}/status", noticeId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.value").value(Status.INACTIVE.toDisplay()));

        // status = live & livedAt exists & change updatedAt/updatedBy
        mvc.perform(get("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(noticeId))
           .andExpect(jsonPath("$.status").value(Status.INACTIVE.toDisplay()))
           .andExpect(jsonPath("$.livedAt").isNotEmpty())
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(27))
           .andExpect(jsonPath("$.updater.name").isNotEmpty());
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 상태 변경: draft -> something > 400")
    @Test
    void changeStatusDraftToSomething(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        String body = """
                      { "value": "something" }
                      """;
        mvc.perform(put("/admin/v1/notices/{0}/status", noticeId)
                            .contentType(APPLICATION_JSON)
                            .content(body))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Failed to read request"));
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 삭제 > 204")
    @Test
    void deleteApi(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        mvc.perform(delete("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isNoContent());
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 삭제: 공지사항 2번 삭제(멱등성) > 204 X 2")
    @Test
    void delete2Times(@Autowired MockMvc mvc) throws Exception {
        create(mvc);
        Integer noticeId = lastNoticeId;
        // 1 times
        mvc.perform(delete("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isNoContent());
        // 2 times
        mvc.perform(delete("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isNoContent());
    }

    @WithAdminUser
    @DisplayName("관리자 공지사항 삭제: 존재하지 않는 공지사항 삭제 > 204")
    @Test
    void deleteNotExists(@Autowired MockMvc mvc) throws Exception {
        Integer noticeId = -1;
        mvc.perform(delete("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isNoContent());
    }

    @WithPartnerUser
    @DisplayName("관리자 공지사항 삭제: 파트너 권한 > 403")
    @Test
    void deleteOnPartner(@Autowired MockMvc mvc) throws Exception {
        Integer noticeId = 1;
        mvc.perform(delete("/admin/v1/notices/{0}", noticeId))
           .andExpect(status().isForbidden());
    }
}
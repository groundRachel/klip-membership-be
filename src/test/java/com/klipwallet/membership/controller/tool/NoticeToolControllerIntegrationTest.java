package com.klipwallet.membership.controller.tool;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithAdminUser;
import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.entity.ArticleStatus;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.repository.NoticeRepository;

import static com.klipwallet.membership.entity.ArticleStatus.INACTIVE;
import static com.klipwallet.membership.entity.ArticleStatus.LIVE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NoticeToolControllerIntegrationTest {
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    ObjectMapper om;

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

    @WithPartnerUser
    @DisplayName("Tool 공지사항 목록 조회 > 200")
    @Test
    void list(@Autowired MockMvc mvc) throws Exception {
        // given
        createSampleNotices();
        // when/then
        mvc.perform(get("/tool/v1/notices"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalElements").value(4L))
           .andExpect(jsonPath("$.totalPages").value(1))
           .andExpect(jsonPath("$.numberOfElements").value(4))
           .andExpect(jsonPath("$.first").value(true))
           .andExpect(jsonPath("$.last").value(true))
           .andExpect(jsonPath("$.content.length()").value(4L))
           .andExpect(jsonPath("$.content[0].id").isNotEmpty())
           .andExpect(jsonPath("$.content[0].title").value("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내"))
           .andExpect(jsonPath("$.content[0].primary").value(false))
           .andExpect(jsonPath("$.content[0].livedAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].creator.id").value(1))
           .andExpect(jsonPath("$.content[0].creator.name").isNotEmpty())
           .andExpect(jsonPath("$.content[0].updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].updater.id").value(4))
           .andExpect(jsonPath("$.content[0].updater.name").isNotEmpty());
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

        results.get(3).changeStatus(LIVE, new MemberId(3));
        results.get(4).changeStatus(LIVE, new MemberId(4));
        results.get(5).changeStatus(LIVE, new MemberId(3));
        results.get(6).changeStatus(LIVE, new MemberId(4));

        results.get(7).changeStatus(INACTIVE, new MemberId(5));
        results.get(8).changeStatus(INACTIVE, new MemberId(6));
        results.get(9).changeStatus(INACTIVE, new MemberId(5));

        results.get(10).deleteBy(new MemberId(6));
        results.get(11).deleteBy(new MemberId(5));

        noticeRepository.saveAll(results);
        noticeRepository.flush();
    }

    @WithAdminUser
    @DisplayName("Tool 공지사항 목록 조회: 파트너 권한이 없으면(관리자일지라도) > 403")
    @Test
    void listOnAdmin(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/notices"))
           .andExpect(status().isForbidden());
        // FIXME @Jordan AccessDeniedException
        //           .andExpect(jsonPath("$.code").value(403_000))
        //           .andExpect(jsonPath("$.err").value("Forbidden"));
    }

    @WithPartnerUser
    @DisplayName("Tool 공지사항 상세 조회 > 200")
    @Test
    void detail(@Autowired MockMvc mvc) throws Exception {
        // given
        Notice notice = createNotice("[안내] app2app 클립 호출 가이드 변경 안내", LIVE);
        Integer noticeId = notice.getId();
        // when/then
        mvc.perform(get("/tool/v1/notices/{0}", noticeId))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(noticeId))
           .andExpect(jsonPath("$.title").value("[안내] app2app 클립 호출 가이드 변경 안내"))
           .andExpect(jsonPath("$.body").value(notice.getBody()))
           .andExpect(jsonPath("$.primary").value(false))
           .andExpect(jsonPath("$.status").value(LIVE.toDisplay()))
           .andExpect(jsonPath("$.livedAt").isNotEmpty())
           .andExpect(jsonPath("$.createdAt").isNotEmpty())
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.creator.id").value(notice.getCreatorId().value()))
           .andExpect(jsonPath("$.creator.name").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(notice.getUpdaterId().value()))
           .andExpect(jsonPath("$.updater.name").isNotEmpty());
    }

    private Notice createNotice(String title, ArticleStatus status) throws IllegalAccessException {
        return createNotice(title, status, false);
    }

    @SuppressWarnings("ConstantValue")
    private Notice createNotice(String title, ArticleStatus status, boolean isPrimary) throws IllegalAccessException {
        Notice notice = new Notice(title, "<p>blah, blah</p>", new MemberId(1));
        if (isPrimary) {
            FieldUtils.writeField(notice, "primary", isPrimary, true);
        }
        notice.changeStatus(status, new MemberId(2));
        return noticeRepository.save(notice);
    }

    @WithPartnerUser
    @DisplayName("Tool 공지사항 상세 조회: 공지사항이 존재하지 않으면 > 404")
    @Test
    void detailNotExists(@Autowired MockMvc mvc) throws Exception {
        // given
        Integer noticeId = Integer.MAX_VALUE;
        // when/then
        mvc.perform(get("/tool/v1/notices/{0}", noticeId))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(404_001))
           .andExpect(jsonPath("$.err").value("공지사항을 찾을 수 없습니다. ID: %s".formatted(noticeId)));
    }

    @WithPartnerUser
    @DisplayName("Tool 공지사항 상세 조회: 공지사항이 존재하지만 LIVE가 아니면 > 404")
    @ParameterizedTest
    @EnumSource(value = ArticleStatus.class, names = "LIVE", mode = Mode.EXCLUDE)
    void detailInactivated(ArticleStatus status, @Autowired MockMvc mvc) throws Exception {
        // given
        Notice notice = createNotice("[안내] App2App 멀티체인 확장 안내 - 폴리곤", status);
        Integer noticeId = notice.getId();
        // when/then
        mvc.perform(get("/tool/v1/notices/{0}", noticeId))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(404_001))
           .andExpect(jsonPath("$.err").value("공지사항을 찾을 수 없습니다. ID: %s".formatted(noticeId)));
    }

    @WithPartnerUser
    @DisplayName("Tool 고정 공지 조회 > 200")
    @Test
    void primary(@Autowired MockMvc mvc) throws Exception {
        // given
        Notice notice = createNotice("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내", LIVE, true);
        Integer primaryNoticeId = notice.getId();
        // when/then
        mvc.perform(get("/tool/v1/notices/primary"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(primaryNoticeId))
           .andExpect(jsonPath("$.title").value("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내"));
    }

    @SuppressWarnings("unused")
    @WithPartnerUser
    @DisplayName("Tool 고정 공지 조회: 고정 공지가 2개인 경우 > 최근 Live 된 것 노출")
    @Test
    void primary2(@Autowired MockMvc mvc) throws Exception {
        // given
        Notice notice1 = createNotice("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내", LIVE, true);
        Notice notice2 = createNotice("[안내] app2app 클립 호출 가이드 변경 안내", LIVE, true);   // 최신 live
        // when/then
        mvc.perform(get("/tool/v1/notices/primary"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(notice2.getId()))
           .andExpect(jsonPath("$.title").value("[안내] app2app 클립 호출 가이드 변경 안내"));
    }

    @WithPartnerUser
    @DisplayName("Tool 고정 공지 조회: 고정 공지가 없으면 최신 공지 노출 > 200")
    @Test
    void primaryFallbackLatestLived(@Autowired MockMvc mvc) throws Exception {
        // given
        @SuppressWarnings("unused")
        Notice notice1 = createNotice("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내.1", LIVE);
        Notice notice2 = createNotice("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내.2", LIVE);
        Integer latestNoticeId = notice2.getId();
        // when/then
        mvc.perform(get("/tool/v1/notices/primary"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(latestNoticeId))
           .andExpect(jsonPath("$.title").value("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내.2"));
    }

    @WithPartnerUser
    @DisplayName("Tool 고정 공지 조회: 공지가 아예 비어 있음. > 204")
    @Test
    void primaryNotFound(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/tool/v1/notices/primary"))
           .andExpect(status().isNoContent());
    }

    @WithPartnerUser
    @DisplayName("Tool 고정 공지 조회: 고정 공지는 있으나 Live 상태 아님. > 204")
    @ParameterizedTest
    @EnumSource(value = ArticleStatus.class, names = "LIVE", mode = Mode.EXCLUDE)
    void primaryNotLive(ArticleStatus status, @Autowired MockMvc mvc) throws Exception {
        // given
        createNotice("[안내] App2App 멀티체인 확장 안내 - 폴리곤", status, true);
        // when/then
        mvc.perform(get("/tool/v1/notices/primary"))
           .andExpect(status().isNoContent());
    }
}
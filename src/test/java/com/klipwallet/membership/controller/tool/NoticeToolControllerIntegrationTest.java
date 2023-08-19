package com.klipwallet.membership.controller.tool;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.Notice;
import com.klipwallet.membership.entity.Notice.Status;
import com.klipwallet.membership.repository.NoticeRepository;

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
        createSampleNotices();

        mvc.perform(get("/tool/v1/notices")
                            .param("status", Status.LIVE.toDisplay()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(4L))
           .andExpect(jsonPath("$[0].id").isNotEmpty())
           .andExpect(jsonPath("$[0].title").value("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내"))
           .andExpect(jsonPath("$[0].primary").value(false))
           .andExpect(jsonPath("$[0].livedAt").isNotEmpty())
           .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
           .andExpect(jsonPath("$[0].creator.id").value(1))
           .andExpect(jsonPath("$[0].creator.name").isNotEmpty())
           .andExpect(jsonPath("$[0].updatedAt").isNotEmpty())
           .andExpect(jsonPath("$[0].updater.id").value(4))
           .andExpect(jsonPath("$[0].updater.name").isNotEmpty());
    }

    /**
     * 0 ~ 2: draft, 3 ~ 6: live, 7 ~ 9: inactive
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
                new Notice("[문서 개선] 클립 NFT 메타데이터 표준 안내 페이지 추가", "10", new MemberId(2)));
        List<Notice> results = noticeRepository.saveAll(notices);

        results.get(3).changeStatus(Status.LIVE, new MemberId(3));
        results.get(4).changeStatus(Status.LIVE, new MemberId(4));
        results.get(5).changeStatus(Status.LIVE, new MemberId(3));
        results.get(6).changeStatus(Status.LIVE, new MemberId(4));

        results.get(7).changeStatus(Status.INACTIVE, new MemberId(5));
        results.get(8).changeStatus(Status.INACTIVE, new MemberId(6));
        results.get(9).changeStatus(Status.INACTIVE, new MemberId(5));

        noticeRepository.saveAll(results);
        noticeRepository.flush();
    }
}
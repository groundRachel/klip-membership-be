package com.klipwallet.membership.controller.tool;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import com.klipwallet.membership.config.security.WithPartnerUser;
import com.klipwallet.membership.entity.ArticleStatus;
import com.klipwallet.membership.entity.Faq;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.ErrorCode;
import com.klipwallet.membership.repository.FaqRepository;

import static com.klipwallet.membership.entity.ArticleStatus.LIVE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FaqToolControllerIntegrationTest {
    @Autowired
    FaqRepository faqRepository;
    @Autowired
    ObjectMapper om;

    @BeforeEach
    void setUp() {
        clearFaqs();
    }

    @AfterEach
    void tearDown() {
        clearFaqs();
    }

    private void clearFaqs() {
        faqRepository.deleteAll();
        faqRepository.flush();
    }

    private void createSampleFaqs() {
        List<Faq> faqs = List.of(
                new Faq("[안내] 월렛커넥트 연동 지원 안내", "1", new MemberId(1)),
                new Faq("[안내] App2App 멀티체인 확장 안내 - 폴리곤", "2", new MemberId(2)),
                new Faq("[안내] App2App Javascript SDK 신규 버전 배포 안내 - 2.2.1", "3", new MemberId(1)),
                new Faq("[안내] app2app 클립 호출 가이드 변경 안내", "4", new MemberId(2)),
                new Faq("[안내] Klip app2app API 개선 (예상 가스비 반환) 및 기타 변경 사항 안내", "5", new MemberId(1)),
                new Faq("[안내] Klip app2app API 개선 (이더리움 및 수수료 대납 지원) 및 기타 변경 사항 안내", "6", new MemberId(2)),
                new Faq("[공지] Klip 지원 자산 변경에 따른 변경 사항 안내", "7", new MemberId(1)),
                new Faq("Klip Developer Forum에 게시글을 작성하기 전, 꼭! 확인해주세요.", "8", new MemberId(2)),
                new Faq("[기능 개선] App2App REST API - execute_contract 및 sign_message 기능 추가", "9", new MemberId(1)),
                new Faq("[문서 개선] 클립 NFT 메타데이터 표준 안내 페이지 추가", "10", new MemberId(2)));
        List<Faq> results = faqRepository.saveAll(faqs);

        results.get(3).changeStatus(LIVE, new MemberId(3));
        results.get(4).changeStatus(LIVE, new MemberId(4));
        results.get(5).changeStatus(LIVE, new MemberId(3));
        results.get(6).changeStatus(LIVE, new MemberId(4));

        results.get(7).changeStatus(ArticleStatus.INACTIVE, new MemberId(5));
        results.get(8).changeStatus(ArticleStatus.INACTIVE, new MemberId(6));
        results.get(9).changeStatus(ArticleStatus.INACTIVE, new MemberId(5));

        faqRepository.saveAll(results);
        faqRepository.flush();
    }

    @WithPartnerUser
    @DisplayName("파트너 FAQ 조회 > 200")
    @Test
    void getFaq(@Autowired MockMvc mvc) throws Exception {
        Faq faq = faqRepository.save(new Faq("멤버십 툴에 어떻게 가입하나요?", "<p>GX 파트너는 누구나 가입할 수 있습니다.</p>", new MemberId(1)));
        faq.changeStatus(LIVE, new MemberId(3));
        faqRepository.save(faq);
        Integer faqId = faq.getId();

        mvc.perform(get("/tool/v1/faqs/{0}", faqId)
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(faqId))
           .andExpect(jsonPath("$.title").value("멤버십 툴에 어떻게 가입하나요?"))
           .andExpect(jsonPath("$.body").value("<p>GX 파트너는 누구나 가입할 수 있습니다.</p>"))
           .andExpect(jsonPath("$.status").value(LIVE.toDisplay()))
           .andExpect(jsonPath("$.livedAt").isNotEmpty())
           .andExpect(jsonPath("$.createdAt").isNotEmpty())
           .andExpect(jsonPath("$.updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.creator.id").value(1))
           .andExpect(jsonPath("$.creator.name").isNotEmpty())
           .andExpect(jsonPath("$.updater.id").value(3))
           .andExpect(jsonPath("$.updater.name").isNotEmpty());
    }

    @WithPartnerUser
    @DisplayName("파트너 FAQ 조회 > 존재하지 않는 FAQ 조회 시도 404")
    @Test
    void getNotExistFaq(@Autowired MockMvc mvc) throws Exception {
        Integer faqId = -2;
        mvc.perform(get("/tool/v1/faqs/{0}", faqId)
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(ErrorCode.FAQ_NOT_FOUND.getCode()))
           .andExpect(jsonPath("$.err").value("FAQ를 찾을 수 없습니다. ID: %d".formatted(faqId)));
    }

    @WithPartnerUser
    @DisplayName("파트너 FAQ 조회 > live 상태가 아닌 FAQ 조회 시도 404")
    @ParameterizedTest
    @EnumSource(value = ArticleStatus.class, names = "LIVE", mode = Mode.EXCLUDE)
    void getNotLiveFaq(ArticleStatus status, @Autowired MockMvc mvc) throws Exception {
        Faq faq = createFaq("적절한 FAQ 제목", status);
        Integer faqId = faq.getId();

        mvc.perform(get("/tool/v1/faqs/{0}", faqId)
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value(ErrorCode.FAQ_NOT_FOUND.getCode()))
           .andExpect(jsonPath("$.err").value("FAQ를 찾을 수 없습니다. ID: %d".formatted(faqId)));
    }

    private Faq createFaq(String title, ArticleStatus status) {
        Faq entity = new Faq(title, "<p>blah, blah</p>", new MemberId(1));
        entity.changeStatus(status, new MemberId(2));
        return faqRepository.save(entity);
    }

    @WithPartnerUser
    @DisplayName("파트너 FAQ 목록 조회 (default (page, size) > 200")
    @Test
    void listFaqWithDefaultQuery(@Autowired MockMvc mvc) throws Exception {
        createSampleFaqs();
        mvc.perform(get("/tool/v1/faqs")
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalElements").value(4))
           .andExpect(jsonPath("$.totalPages").value(1))
           .andExpect(jsonPath("$.content[0].id").isNotEmpty())
           .andExpect(jsonPath("$.content[0].title").isNotEmpty())
           .andExpect(jsonPath("$.content[0].body").doesNotExist())
           .andExpect(jsonPath("$.content[0].livedAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].creator.name").isNotEmpty())
           .andExpect(jsonPath("$.content[0].updater.name").isNotEmpty())
           .andExpect(jsonPath("$.content[0].creator.id").isNotEmpty())
           .andExpect(jsonPath("$.content[0].updater.id").isNotEmpty())
           .andExpect(jsonPath("$.content[0].status").value(LIVE.toDisplay()))
           .andExpect(jsonPath("$.content[1].status").value(LIVE.toDisplay()))
           .andExpect(jsonPath("$.content[2].status").value(LIVE.toDisplay()))
           .andExpect(jsonPath("$.content[3].status").value(LIVE.toDisplay()));
    }

    @WithPartnerUser
    @DisplayName("파트너 FAQ 목록 조회 (size = 1, page = 2 > 200")
    @Test
    void listFaqWithPageSize(@Autowired MockMvc mvc) throws Exception {
        createSampleFaqs();
        mvc.perform(get("/tool/v1/faqs?size=1&page=2")
                            .contentType(APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalElements").value(4))
           .andExpect(jsonPath("$.totalPages").value(4))
           .andExpect(jsonPath("$.content[0].id").isNotEmpty())
           .andExpect(jsonPath("$.content[0].title").isNotEmpty())
           .andExpect(jsonPath("$.content[0].body").doesNotExist())
           .andExpect(jsonPath("$.content[0].livedAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].updatedAt").isNotEmpty())
           .andExpect(jsonPath("$.content[0].creator.name").isNotEmpty())
           .andExpect(jsonPath("$.content[0].updater.name").isNotEmpty())
           .andExpect(jsonPath("$.content[0].creator.id").isNotEmpty())
           .andExpect(jsonPath("$.content[0].updater.id").isNotEmpty())
           .andExpect(jsonPath("$.content[0].status").value(LIVE.toDisplay()));
    }
}

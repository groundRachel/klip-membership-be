package com.klipwallet.membership.controller.tool;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import com.klipwallet.membership.dto.operator.OperatorSummary;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.Partner;
import com.klipwallet.membership.repository.OperatorRepository;
import com.klipwallet.membership.repository.PartnerRepository;
import com.klipwallet.membership.service.KlipAccountService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OperatorToolControllerIntegrationTest {

    @Autowired
    OperatorRepository operatorRepository;
    @Autowired
    ObjectMapper om;
    @MockBean
    PartnerRepository partnerRepository;
    @MockBean
    KlipAccountService klipAccountService;

    private Long lastOperatorId;

    @BeforeEach
    void setUp() {
        clearOperators();
    }

    @AfterEach
    void tearDown() {
        clearOperators();
    }

    public void clearOperators() {
        operatorRepository.deleteAll();
        operatorRepository.flush();
    }

    @WithPartnerUser
    @DisplayName("운영자 생성 > 201")
    @Test
    void create(@Autowired MockMvc mvc) throws Exception {
        Partner partner = new Partner();
        FieldUtils.writeField(partner, "id", 23, true);
        given(partnerRepository.findById(any())).willReturn(Optional.of(partner));
        given(klipAccountService.getKlaytnAddress(any())).willReturn(new Address("0x1dD60F6Bc03e986aD2a07298DB096d6122178587"));
        given(klipAccountService.getKlipUser(any())).willReturn(new KlipAccount(1L, "2538023920", "testemail@test.com", "010-1234-5678"));
        String body = """
                      {
                        "klipId": 1,
                        "klipRequestKey": "84cdc4cc-da8a-42ab-ae11-eb988c3b3f42"
                      }
                      """;
        var ra = mvc.perform(post("/tool/v1/operators")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isString());
        setLastOperatorId(ra);
    }

    private void setLastOperatorId(ResultActions ra) throws IOException {
        MvcResult mvcResult = ra.andReturn();
        OperatorSummary summary = om.readValue(mvcResult.getResponse().getContentAsString(), OperatorSummary.class);
        lastOperatorId = summary.id();

    }
}
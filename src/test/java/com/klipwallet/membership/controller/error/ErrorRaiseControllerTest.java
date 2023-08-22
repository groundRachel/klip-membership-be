package com.klipwallet.membership.controller.error;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ErrorRaiseControllerTest {
    @Test
    void raiseHttpRequestMethodNotSupportedException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/1"))
           .andExpect(status().isMethodNotAllowed())    // 405
           .andExpect(jsonPath("$.code").value(500_999))    // 405 -> 500999
           .andExpect(jsonPath("$.err").value("Method 'POST' is not supported."));
    }

    @Test
    void raiseHttpMediaTypeNotSupportedException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/14"))
           .andExpect(status().isUnsupportedMediaType())    // 415
           .andExpect(jsonPath("$.code").value(500_999))    // 415 -> 500999
           .andExpect(jsonPath("$.err").value("Content-Type 'text/xml' is not supported."));
    }

    @Test
    void raiseHttpMediaTypeNotAcceptableException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/2"))
           .andExpect(status().isNotAcceptable())    // 406
           .andExpect(jsonPath("$.code").value(500_999))    // 500999
           .andExpect(jsonPath("$.err").value("Acceptable representations: [application/json]."));
    }

    @Test
    void raiseMissingPathVariableException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/3"))
           .andExpect(status().isInternalServerError())    // 500
           .andExpect(jsonPath("$.code").value(500_000))
           .andExpect(jsonPath("$.err").value("Required path variable 'code' is not present."));
    }

    @Test
    void raiseMissingServletRequestParameterException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/4"))
           .andExpect(status().isBadRequest())    // 400
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Required parameter 'code' is not present."));
    }

    @Test
    void raiseMissingServletRequestPartException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/5"))
           .andExpect(status().isBadRequest())    // 400
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Required part 'somePart' is not present."));
    }

    @Test
    void raiseServletRequestBindingException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/6"))
           .andExpect(status().isBadRequest())    // 400
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Required header 'X-Some-Header' is not present."));
    }

    @Test
    void raiseNoHandlerFoundException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/7"))
           .andExpect(status().isNotFound())    // 400
           .andExpect(jsonPath("$.code").value(404_000))
           .andExpect(jsonPath("$.err").value("No endpoint GET /7/no-handler."));
    }

    @Test
    void raiseAsyncRequestTimeoutException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/8"))
           .andExpect(status().isServiceUnavailable())    // 503
           .andExpect(jsonPath("$.code").value(503_001))
           .andExpect(jsonPath("$.err").value("Service Unavailable Due to async timeout."));
    }

    @Test
    void raiseErrorResponseException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/9"))
           .andExpect(status().isGone())    // ErrorResponse 설정에 따라 달라짐. 여기에서는 410
           .andExpect(jsonPath("$.code").value(500_999))    // 예시로 설정한 410이 없어서 500999코드 반환
           .andExpect(jsonPath("$.err").value("Gone"));
    }

    @Test
    void raiseConversionNotSupportedException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/10"))
           .andExpect(status().isInternalServerError())    // 500
           .andExpect(jsonPath("$.code").value(500_000))    // 500
           .andExpect(jsonPath("$.err").value("Failed to convert 'null' with value: 'lived'"));
    }

    @Test
    void raiseTypeMismatchException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/11")
                            .param("code", "string"))   // Integer 이지만 강제로 문자열
           .andExpect(status().isBadRequest())    // 400
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Failed to convert 'code' with value: 'string'"));
    }

    @Test
    void raiseHttpMessageNotReadableException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/12"))
           .andExpect(status().isBadRequest())    // 400
           .andExpect(jsonPath("$.code").value(400_000))
           .andExpect(jsonPath("$.err").value("Failed to read request"));
    }

    @Test
    void raiseHttpMessageNotWritableException(@Autowired MockMvc mvc) throws Exception {
        mvc.perform(get("/error/raise/13"))
           .andExpect(status().isInternalServerError())    // 500
           .andExpect(jsonPath("$.code").value(500_000))
           .andExpect(jsonPath("$.err").value("Failed to write request"));
    }
}
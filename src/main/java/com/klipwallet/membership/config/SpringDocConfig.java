package com.klipwallet.membership.config;

import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import com.klipwallet.membership.config.security.KlipMembershipOAuth2User;
import com.klipwallet.membership.controller.GlobalRestControllerAdvice;
import com.klipwallet.membership.controller.admin.AttachFileAdminController;
import com.klipwallet.membership.controller.admin.NoticeAdminController;
import com.klipwallet.membership.dto.notice.NoticeDto.Create;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.AuthenticatedUser;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.ConflictException;
import com.klipwallet.membership.exception.ForbiddenException;
import com.klipwallet.membership.exception.InvalidRequestException;
import com.klipwallet.membership.exception.NoticeNotFoundException;

import static com.klipwallet.membership.config.SecurityConfig.ROLE_PARTNER;
import static com.klipwallet.membership.controller.GlobalRestControllerAdvice.toProblemDetail;
import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;
import static org.springdoc.core.utils.SpringDocUtils.getConfig;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
public class SpringDocConfig {
    private final ObjectMapper mapper;
    private final GlobalRestControllerAdvice globalRestControllerAdvice;
    private final NoticeAdminController noticeAdminController;

    public SpringDocConfig(ObjectMapper mapper,
                           GlobalRestControllerAdvice globalRestControllerAdvice,
                           NoticeAdminController noticeAdminController) {
        this.mapper = mapper;
        this.globalRestControllerAdvice = globalRestControllerAdvice;
        this.noticeAdminController = noticeAdminController;
        initCustomSchema();
    }

    private void initCustomSchema() {
        getConfig().replaceWithSchema(Long.class, new Schema<Long>().type("string").format("int64").example("2147483648"))
                   .replaceWithSchema(Address.class,
                                      new Schema<Address>().type("string").format("hex").description("블록체인 주소(hex)").example(exampleOfAddress()));
    }

    private Address exampleOfAddress() {
        return new Address("0xa005e82487fb629923b9598f0fd1c2e9499f0cab");
    }

    @Bean
    public OpenAPI defaultOpenApi(@Value("${application.version}") String version) {
        return new OpenAPI()
                       .info(new Info().title("Klip Membership Tool API")
                                       .description("Klip Membership Tool Management & Internal API")
                                       .version(version))
                       .components(new Components()
                                           .addSecuritySchemes("km-session",
                                                               new SecurityScheme().type(Type.APIKEY)
                                                                                   .in(In.COOKIE)
                                                                                   .name("KMSESSION"))
                                           .addSecuritySchemes("kakao-token",
                                                               new SecurityScheme().type(Type.APIKEY)
                                                                                   .in(In.HEADER)
                                                                                   .name(HttpHeaders.AUTHORIZATION)
                                                                                   .description("Kakao {AccessToken}"))
                                           .addSchemas("Error400", problemDetail400Schema())
                                           .addSchemas("Error400Fields", problemDetail400FieldsSchema())
                                           .addSchemas("Error400File", problemDetail400FileSchema())
                                           .addSchemas("Error401", problemDetail401Schema())
                                           .addSchemas("Error403", problemDetail403Schema())
                                           .addSchemas("Error404", problemDetail404Schema())
                                           .addSchemas("Error409", problemDetail409Schema())
                                           .addSchemas("Error500", problemDetail500Schema()));
    }

    @SuppressWarnings("rawtypes")
    private Schema problemDetail400FieldsSchema() {
        Method method = MethodUtils.getAccessibleMethod(NoticeAdminController.class, "create", Create.class, AuthenticatedUser.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        BindException create = new BindException(noticeAdminController, "create");
        create.addError(new FieldError("create", "title", "title: '크기가 1에서 200 사이여야 합니다'"));
        create.addError(new FieldError("create", "body", "body: '본문은 비어있으면 안됩니다.'"));
        MethodArgumentNotValidException cause = new MethodArgumentNotValidException(methodParameter, create);
        var pdJson = toJson(globalRestControllerAdvice.toProblemDetail(cause));
        return new Schema<>().type("object").example(pdJson);
    }

    @SuppressWarnings("rawtypes")
    private Schema problemDetail400FileSchema() {
        Method method = MethodUtils.getAccessibleMethod(AttachFileAdminController.class, "uploadImage", MultipartFile.class, AuthenticatedUser.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        BindException create = new BindException(noticeAdminController, "upload");
        create.addError(new FieldError("upload", "upload.file", "이미지 파일만 업로드 가능합니다.(jpeg, png)"));
        MethodArgumentNotValidException cause = new MethodArgumentNotValidException(methodParameter, create);
        var pdJson = toJson(globalRestControllerAdvice.toProblemDetail(cause));
        return new Schema<>().type("object").example(pdJson);
    }

    @SuppressWarnings("rawtypes")
    private Schema problemDetail400Schema() {
        var pdJson = toJson(toProblemDetail(BAD_REQUEST, new InvalidRequestException(), "공지사항을 찾을 수 없습니다. ID: 3322"));
        return new Schema<>().type("object").example(pdJson);
    }

    @SuppressWarnings("rawtypes")
    private Schema problemDetail401Schema() {
        var pdJson = toJson(toProblemDetail(new OAuth2AuthenticationException(
                new OAuth2Error("GOOGLE_ERROR_CODE", "GOOGLE_ERROR_MESSAGE", "https://google.io/oauth/GOOGLE_ERROR_CODE"))));
        return new Schema<>().type("object").example(pdJson);
    }

    @SuppressWarnings("rawtypes")
    private Schema problemDetail403Schema() {
        var pdJson = toJson(toProblemDetail(FORBIDDEN, new ForbiddenException(oauth2User()), "권한이 부족합니다. ROLE_PARTNER"));
        return new Schema<>().type("object").example(pdJson);
    }

    @SuppressWarnings("rawtypes")
    private Schema problemDetail409Schema() {
        var pdJson = toJson(toProblemDetail(CONFLICT, new ConflictException(), "이미 처리된 요청입니다. ID: 3322, 처리상태: approved, 처리자: 8, 처리시각: {3}"));
        return new Schema<>().type("object").example(pdJson);
    }

    private KlipMembershipOAuth2User oauth2User() {
        return new KlipMembershipOAuth2User(new MemberId(11), AuthorityUtils.createAuthorityList(ROLE_PARTNER), "정조던",
                                            "jordan.jung@groundx.xyz");

    }

    @SuppressWarnings("rawtypes")
    private Schema problemDetail404Schema() {
        var pdJson = toJson(toProblemDetail(NOT_FOUND, new NoticeNotFoundException(3322), "공지사항을 찾을 수 없습니다. ID: 3322"));
        return new Schema<>().type("object").example(pdJson);
    }

    @SuppressWarnings("rawtypes")
    private Schema problemDetail500Schema() {
        var psJson = toJson(toProblemDetail(new Exception("Server Error")));
        return new Schema<ProblemDetail>().type("object").example(psJson);
    }

    public String toJson(Object target) {
        try {
            return mapper.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Bean
    public GroupedOpenApi all() {
        return GroupedOpenApi.builder()
                             .group("0. All")
                             .pathsToMatch("/**")
                             .displayName("0. ALL")
                             .build();
    }

    @Bean
    public GroupedOpenApi toolApi() {
        return GroupedOpenApi.builder()
                             .group("1. klip-membership-tool.tool")
                             .pathsToMatch("/tool/**")
                             .displayName("1. 파트너를 위한 Tool API")
                             .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                             .group("2. klip-membership-tool.admin")
                             .pathsToMatch("/admin/**")
                             .displayName("2. 관리자를 위한 Admin API")
                             .build();
    }

    @Bean
    public GroupedOpenApi externalApi() {
        return GroupedOpenApi.builder()
                             .group("3. klip-membership-tool.external")
                             .pathsToMatch("/open/**", "/internal/**")
                             .displayName("3. 외부 제공 API")
                             .build();
    }
}

package com.klipwallet.membership.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import com.klipwallet.membership.config.security.KlipMembershipOAuth2User;
import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.exception.ConflictException;
import com.klipwallet.membership.exception.ForbiddenException;
import com.klipwallet.membership.exception.InvalidRequestException;
import com.klipwallet.membership.exception.NoticeNotFoundException;

import static com.klipwallet.membership.config.SecurityConfig.ROLE_PARTNER;
import static com.klipwallet.membership.controller.GlobalRestControllerAdvice.toProblemDetail;
import static java.util.Collections.emptyMap;
import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;
import static org.springdoc.core.utils.SpringDocUtils.getConfig;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
public class SpringDocConfig {
    private final ObjectMapper mapper;

    public SpringDocConfig(ObjectMapper mapper) {
        this.mapper = mapper;
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
                       .components(new Components().addSchemas("Error400", problemDetail400Schema())
                                                   .addSchemas("Error401", problemDetail401Schema())
                                                   .addSchemas("Error403", problemDetail403Schema())
                                                   .addSchemas("Error404", problemDetail404Schema())
                                                   .addSchemas("Error409", problemDetail409Schema())
                                                   .addSchemas("Error500", problemDetail500Schema()));
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

    private Schema problemDetail409Schema() {
        var pdJson = toJson(toProblemDetail(CONFLICT, new ConflictException(), "이미 처리된 요청입니다. ID: 3322, 처리상태: approved, 처리자: 8, 처리시각: {3}"));
        return new Schema<>().type("object").example(pdJson);
    }

    private KlipMembershipOAuth2User oauth2User() {
        return new KlipMembershipOAuth2User(new MemberId(11), emptyMap(), AuthorityUtils.createAuthorityList(ROLE_PARTNER), "정조던",
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

    //
    //    @Bean
    //    public GroupedOpenApi toolApi() {
    //        return GroupedOpenApi.builder()
    //                             .group("1. klip-membership-tool.tool")
    //                             .pathsToMatch("/tool/**")
    //                             .displayName("1. 파트너를 위한 Tool API")
    //                             .build();
    //    }
    //
    //    @Bean
    //    public GroupedOpenApi adminApi() {
    //        return GroupedOpenApi.builder()
    //                             .group("2. klip-membership-tool.admin")
    //                             .pathsToMatch("/admin/**")
    //                             .displayName("2. 관리자를 위한 Admin API")
    //                             .build();
    //    }
    //
    //    @Bean
    //    public GroupedOpenApi externalApi() {
    //        return GroupedOpenApi.builder()
    //                             .group("3. klip-membership-tool.external")
    //                             .pathsToMatch("/open/**", "/internal/**")
    //                             .displayName("3. 외부 제공 API")
    //                             .build();
    //    }
    //
    //    @Bean
    //    public GroupedOpenApi all() {
    //        return GroupedOpenApi.builder()
    //                             .group("0. All")
    //                             .pathsToMatch("/**")
    //                             .displayName("0. ALL")
    //                             .build();
    //    }
}

package com.klipwallet.membership.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.klipwallet.membership.entity.Address;
import com.klipwallet.membership.entity.UserId;

import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;
import static org.springdoc.core.utils.SpringDocUtils.getConfig;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
@ConditionalOnExpression("${springdoc.api-docs.enabled:true}")
public class SpringDocConfig {
    public SpringDocConfig() {
        initCustomSchema();
    }

    private void initCustomSchema() {
        getConfig().replaceWithSchema(Long.class, new Schema<Long>().type("string").format("int64").example("2147483648"))
                   .replaceWithSchema(Address.class, new Schema<Address>().type("string").format("hex").description("블록체인 주소(hex)")
                                                                          .example(new Address("0xa005e82487fb629923b9598f0fd1c2e9499f0cab")))
                   .replaceWithSchema(UserId.class, new Schema<UserId>().type("integer").format("int32").description("멤버 ID")
                                                                        .example(new UserId(182)));
    }

    @Bean
    public OpenAPI defaultOpenApi(@Value("${application.version}") String version) {
        return new OpenAPI()
                       .info(new Info().title("Klip Membership Tool API")
                                       .description("Klip Membership Tool Management & Internal API")
                                       .version(version));
    }

    @Bean
    public GroupedOpenApi manageApi() {
        return GroupedOpenApi.builder()
                             .group("1.klip-membership-tool.manage")
                             .pathsToMatch("/manage/**")
                             .displayName("1. 파트너서와 관리자를 위한 Tool API")
                             .build();
    }

    @Bean
    public GroupedOpenApi internalApi() {
        return GroupedOpenApi.builder()
                             .group("2.klip-membership-tool.internal")
                             .pathsToMatch("/internal/**")
                             .displayName("2. 사내용 API")
                             .build();
    }
}

package com.klipwallet.membership.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.klipwallet.membership.entity.Address;

import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;
import static org.springdoc.core.utils.SpringDocUtils.getConfig;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
public class SpringDocConfig {
    public SpringDocConfig() {
        initCustomSchema();
    }

    private void initCustomSchema() {
        getConfig().replaceWithSchema(Long.class, new Schema<Long>().type("string").format("int64").example("2147483648"))
                   .replaceWithSchema(Address.class, new Schema<Address>().type("string").format("hex").description("블록체인 주소(hex)")
                                                                          .example(new Address("0xa005e82487fb629923b9598f0fd1c2e9499f0cab")));
    }

    @Bean
    public OpenAPI defaultOpenApi(@Value("${application.version}") String version) {
        return new OpenAPI()
                       .info(new Info().title("Klip Membership Tool API")
                                       .description("Klip Membership Tool Management & Internal API")
                                       .version(version));
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

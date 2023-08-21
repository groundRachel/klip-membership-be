package com.klipwallet.membership.controller;

import java.net.URI;

import io.swagger.v3.oas.annotations.Hidden;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.klipwallet.membership.dto.MainMeta;

@Hidden
@RestController
public class MainController {
    private final SwaggerUiConfigParameters swaggerProperties;
    private MainMeta mainMeta;

    public MainController(@Autowired(required = false) SwaggerUiConfigParameters swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @GetMapping({"", "/"})
    public MainMeta root() {
        if (mainMeta == null) {
            return newMainMeta();
        }
        return mainMeta;
    }

    private MainMeta newMainMeta() {
        URI origin = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();
        URI swaggerUrl = tryGetSwaggerUrl(origin);
        return new MainMeta("Klip Membership API", origin, swaggerUrl);
    }

    private URI tryGetSwaggerUrl(URI origin) {
        if (swaggerProperties == null) {
            return null;
        }
        return UriComponentsBuilder.fromUri(origin).path(swaggerProperties.getPath()).build().toUri();
    }
}

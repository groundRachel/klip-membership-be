package com.klipwallet.membership.dto;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record MainMeta(String title, URI origin, URI swaggerUrl) {
}

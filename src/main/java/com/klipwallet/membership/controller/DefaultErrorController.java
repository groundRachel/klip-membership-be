package com.klipwallet.membership.controller;

import java.util.List;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;

public class DefaultErrorController extends BasicErrorController {
    public DefaultErrorController(ErrorAttributes errorAttributes,
                                  ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    public DefaultErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
                                  List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorProperties, errorViewResolvers);
    }


}

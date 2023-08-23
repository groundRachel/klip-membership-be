package com.klipwallet.membership.adaptor.spring.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {
    public static final MediaType IMAGE_ALL = MediaType.valueOf("image/*");

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        String contentType = value.getContentType();
        return isImage(contentType);
    }

    private boolean isImage(String contentType) {
        if (contentType == null) {
            return false;
        }
        try {
            MediaType mediaType = MediaType.valueOf(contentType);
            return IMAGE_ALL.includes(mediaType);
        } catch (InvalidMediaTypeException cause) {
            log.warn("Invalid Media-Type: {}", contentType, cause);
            return false;
        }
    }

}

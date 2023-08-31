package com.klipwallet.membership.adaptor.spring.validation;

import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {
    public static final Set<MediaType> SUPPORTED_IMAGES = Set.of(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG);

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        String contentType = value.getContentType();
        return isSupported(contentType);
    }

    private boolean isSupported(String contentType) {
        if (contentType == null) {
            return false;
        }
        try {
            MediaType mediaType = MediaType.valueOf(contentType);
            return SUPPORTED_IMAGES.contains(mediaType);
        } catch (InvalidMediaTypeException cause) {
            log.warn("Invalid Media-Type: {}", contentType, cause);
            return false;
        }
    }

}

package com.klipwallet.membership.adaptor.s3;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.klipwallet.membership.entity.MemberId;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Disabled("직접 이미지 올리는 테스트이기 때문에 Disabled")
class S3AdaptorTest {
    @Autowired
    S3Adaptor s3Adaptor;

    @Test
    void store() throws IOException {
        String fileName = "test";
        String contentType = MediaType.IMAGE_JPEG_VALUE;
        ClassPathResource resource = new ClassPathResource("/testimage/test.jpg");
        MultipartFile mockImage = new MockMultipartFile(fileName, fileName + "." + contentType, contentType, resource.getInputStream());
        s3Adaptor.store(new S3Object(mockImage), new MemberId(1));
    }
}
package com.klipwallet.membership.adaptor.s3;

import java.io.IOException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import com.klipwallet.membership.dto.storage.StorageResult;
import com.klipwallet.membership.entity.Attachable;
import com.klipwallet.membership.entity.MemberId;
import com.klipwallet.membership.entity.ObjectId;
import com.klipwallet.membership.exception.storage.StorageStoreException;
import com.klipwallet.membership.service.StorageService;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Adaptor implements StorageService {
    private static final String CLOUDFRONT_PROTOCOL = "https";
    private static final String S3_SUB_PREFIX = "klip-membership";
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.prefix}")
    private String s3Prefix;
    @Value("${cloud.aws.cloudfront.distribution-domain}")
    private String distributionDomain;

    @Override
    public StorageResult store(Attachable command, MemberId memberId) {
        String path = "%s/%s/%s".formatted(S3_SUB_PREFIX, String.valueOf(memberId.value()), UUID.randomUUID());
        String key = "%s/%s".formatted(s3Prefix, path);
        PutObjectRequest req = PutObjectRequest.builder().bucket(bucket).contentType(command.getContentType().toString()).key(key).build();
        RequestBody body;
        try {
            body = RequestBody.fromInputStream(command.getInputStream(), command.getBytesSize());
        } catch (IOException e) {
            throw new StorageStoreException(e);
        }
        PutObjectResponse res = s3Client.putObject(req, body);
        if (!res.sdkHttpResponse().isSuccessful()) {
            throw new StorageStoreException(String.valueOf(res.sdkHttpResponse().statusCode()));
        }
        return new StorageResult(new ObjectId(key), "%s://%s/%s".formatted(CLOUDFRONT_PROTOCOL, distributionDomain, path));
    }
}

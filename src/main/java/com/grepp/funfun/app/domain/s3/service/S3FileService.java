package com.grepp.funfun.app.domain.s3.service;

import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Template s3Template;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public void upload(MultipartFile file, String filename) {
        try {
            s3Template.upload(bucketName, filename, file.getInputStream());
        } catch(IOException e) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
    }

    public void delete(String filename) {
        try {
            s3Template.deleteObject(bucketName, filename);
        } catch (Exception e) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
    }
}


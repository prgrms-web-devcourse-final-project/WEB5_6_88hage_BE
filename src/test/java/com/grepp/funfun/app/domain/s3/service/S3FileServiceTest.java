package com.grepp.funfun.app.domain.s3.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class S3FileServiceTest {

    @Autowired
    private S3FileService s3FileService;

    @Test
    @DisplayName("진짜 S3 업로드 테스트")
    void realS3Upload() {
        MultipartFile file = new MockMultipartFile("file", "real-test.jpg", "image/jpeg",
            "real data".getBytes());

        s3FileService.upload(file, "real-test.jpg");

    }
}


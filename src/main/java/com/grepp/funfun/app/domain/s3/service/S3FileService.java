package com.grepp.funfun.app.domain.s3.service;

import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    @Value("${cloud.aws.s3.url}")
    private String s3BaseUrl;

    public String upload(MultipartFile file, String depth) {
        if (file == null || file.isEmpty()) return null;

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new CommonException(ResponseCode.INVALID_FILENAME);
        }

        String ext = getExtension(originalFilename);
        String renameFilename = UUID.randomUUID() + ext;
        String fullPath = depth + "/" + renameFilename;

        try {
            s3Template.upload(bucketName, fullPath, file.getInputStream());
        } catch (IOException e) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "S3 업로드 실패");
        }

        return s3BaseUrl + fullPath;
    }

    // 다중 파일 업로드
    public List<String> upload(List<MultipartFile> files, String depth) {
        if (files == null || files.isEmpty()) return List.of();

        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String uploadedUrl = upload(file, depth);
                uploadedUrls.add(uploadedUrl);
            }
        }

        return uploadedUrls;
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}

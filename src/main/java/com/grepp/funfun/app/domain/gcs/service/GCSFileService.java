package com.grepp.funfun.app.domain.gcs.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
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
public class GCSFileService {

    @Value("${google.cloud.storage.bucket}")
    private String bucket;

    private final String storageBaseUrl = "https://storage.googleapis.com/";

    // 단일 파일 업로드
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
            Storage storage = StorageOptions.getDefaultInstance().getService();
            BlobId blobId = BlobId.of(bucket, fullPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
            storage.create(blobInfo, file.getBytes());
        } catch (IOException e) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "GCS 업로드 실패");
        }

        return storageBaseUrl + bucket + "/" + fullPath;
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

    // 파일 삭제
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        if (!imageUrl.startsWith(storageBaseUrl + bucket + "/")) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "GCS URL 형식이 아닙니다.");
        }

        String objectKey = imageUrl.replace(storageBaseUrl + bucket + "/", "");

        try {
            Storage storage = StorageOptions.getDefaultInstance().getService();
            boolean deleted = storage.delete(bucket, objectKey);
            if (!deleted) {
                throw new CommonException(ResponseCode.NOT_FOUND, "GCS 객체를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR, "GCS 이미지 삭제 실패");
        }
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}

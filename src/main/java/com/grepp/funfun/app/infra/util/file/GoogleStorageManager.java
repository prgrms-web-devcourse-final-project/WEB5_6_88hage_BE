package com.grepp.funfun.app.infra.util.file;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GoogleStorageManager{
    
    @Value("${google.cloud.storage.bucket}")
    private String bucket;
    private final String storageBaseUrl = "https://storage.googleapis.com/";

    public String upload(MultipartFile file, String depth) throws IOException {
        if (file.isEmpty()) return null;

        if (file.getOriginalFilename() == null) {
            throw new CommonException(ResponseCode.INVALID_FILENAME);
        }

        String originFileName = file.getOriginalFilename();
        String renameFileName = generateRenameFileName(originFileName);
        String objectPath = depth + "/" + renameFileName;
        String url = storageBaseUrl + bucket + "/" + objectPath;

        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of(bucket, objectPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(file.getContentType())
            .build();

        Blob blob = storage.create(blobInfo, file.getBytes());

        return url;
    }

    protected String generateRenameFileName(String originFileName) {
        String ext = originFileName.substring(originFileName.lastIndexOf("."));
        return UUID.randomUUID() + ext;
    }
}

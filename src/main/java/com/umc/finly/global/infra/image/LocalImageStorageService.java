package com.umc.finly.global.infra.image;

import com.umc.finly.domain.member.exception.code.MemberErrorCode;
import com.umc.finly.global.apiPayload.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Slf4j
@Service
public class LocalImageStorageService implements ImageStorageService{

    @Value("${image.upload-dir}")
    private String uploadRootDir;

    @Override
    public String upload(MultipartFile file, String dirName) {
        validateImage(file);

        // 저장 디렉토리 생성
        String uploadDirPath = uploadRootDir + "/" + dirName;
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 파일명 생성 (중복 방지)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new CustomException(MemberErrorCode.INVALID_IMAGE_FILE);
        }
        originalFilename = StringUtils.cleanPath(originalFilename);
        String extension = extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + "." + extension;

        File savedFile = new File(uploadDir, storedFilename);

        try {
            file.transferTo(savedFile);
        } catch (IOException e) {
            log.error("[ImageUpload] failed", e);
            throw new CustomException(MemberErrorCode.IMAGE_UPLOAD_FAILED);
        }

        /**
         * DB에는 이 URL만 저장
         * (예: /images/profile/uuid.png)
         */
        return "/images/" + dirName + "/" + storedFilename;
    }


    @Override
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String filePath = uploadRootDir + imageUrl.replace("/images", "");
        File file = new File(filePath);

        if (file.exists() && !file.delete()) {
            log.warn("[ImageDelete] failed: {}", filePath);
        }
    }

    // ---------------- private helpers ----------------
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(MemberErrorCode.INVALID_IMAGE_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(MemberErrorCode.INVALID_IMAGE_FILE);
        }
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf(".");
        if (idx == -1) {
            throw new CustomException(MemberErrorCode.INVALID_IMAGE_FILE);
        }
        return filename.substring(idx + 1);
    }
}

package com.umc.finly.global.infra.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String upload(MultipartFile file, String dirName);
    void delete(String imageUrl);
}

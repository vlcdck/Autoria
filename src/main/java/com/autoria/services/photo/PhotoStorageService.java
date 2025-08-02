package com.autoria.services.photo;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoStorageService {
    String savePhoto(MultipartFile file) throws IOException;

    void deletePhoto(String photoUrl) throws IOException;
}

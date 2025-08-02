package com.autoria.controllers;

import com.autoria.services.photo.CarAdPhotoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class CarAdPhotoController {

    private final CarAdPhotoService carAdPhotoService;

    @PreAuthorize("hasAnyRole('SELLER', 'MANAGER', 'ADMIN')")
    @PostMapping("/photos/{adId}")
    public ResponseEntity<String> uploadPhoto(@PathVariable UUID adId,
                                              @RequestParam("file") MultipartFile file,
                                              Authentication auth) throws IOException {
        try {
            String url = carAdPhotoService.uploadPhoto(adId, file, auth);
            return ResponseEntity.ok(url);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('SELLER', 'MANAGER', 'ADMIN')")
    @PostMapping("/photos/multiple/{adId}")
    public ResponseEntity<List<String>> uploadMultiplePhotos(@PathVariable UUID adId,
                                                             @RequestParam("files") List<MultipartFile> files,
                                                             Authentication auth) {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<String> urls = carAdPhotoService.uploadPhotos(adId, files, auth);
            return ResponseEntity.ok(urls);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAnyRole('SELLER', 'MANAGER', 'ADMIN')")
    @DeleteMapping("/photos/{adId}")
    public ResponseEntity<String> deletePhoto(@PathVariable UUID adId,
                                              @RequestParam("photoUrl") String photoUrl,
                                              Authentication auth) throws IOException {
        try {
            carAdPhotoService.deletePhoto(adId, photoUrl, auth);
            return ResponseEntity.ok("Photo deleted");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}


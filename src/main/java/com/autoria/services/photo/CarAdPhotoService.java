package com.autoria.services.photo;

import com.autoria.models.ad.CarAd;
import com.autoria.models.user.AppUser;
import com.autoria.repository.CarAdRepository;
import com.autoria.security.user.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarAdPhotoService {

    private final PhotoStorageService photoStorageService;
    private final CarAdRepository carAdRepository;

    public String uploadPhoto(UUID adId, MultipartFile file, Authentication auth) throws IOException {
        CarAd ad = getCarAdWithAccessCheck(adId, auth);

        String url = photoStorageService.savePhoto(file);

        if (ad.getPhotos() == null) {
            ad.setPhotos(new ArrayList<>());
        }

        ad.getPhotos().add(url);
        carAdRepository.save(ad);

        return url;
    }

    public void deletePhoto(UUID adId, String photoUrl, Authentication auth) throws IOException {
        CarAd ad = getCarAdWithAccessCheck(adId, auth);

        if (ad.getPhotos() == null || !ad.getPhotos().contains(photoUrl)) {
            throw new EntityNotFoundException("Photo not found in ad");
        }

        ad.getPhotos().remove(photoUrl);
        carAdRepository.save(ad);

        photoStorageService.deletePhoto(photoUrl);
    }

    public List<String> uploadPhotos(UUID adId, List<MultipartFile> files, Authentication auth) {
        CarAd ad = getCarAdWithAccessCheck(adId, auth);
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String url = photoStorageService.savePhoto(file);
                urls.add(url);
            } catch (IOException e) {
                log.warn("Failed to upload file: {}", file.getOriginalFilename(), e);
            }
        }

        if (ad.getPhotos() == null) {
            ad.setPhotos(new ArrayList<>());
        }

        ad.getPhotos().addAll(urls);
        carAdRepository.save(ad);

        return urls;
    }

    private CarAd getCarAdWithAccessCheck(UUID adId, Authentication auth) {
        CarAd ad = carAdRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));

        AppUserDetails userDetails = (AppUserDetails) auth.getPrincipal();
        AppUser currentUser = userDetails.getAppUser();

        boolean isPrivileged = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER") || a.getAuthority().equals("ROLE_ADMIN"));

        if (!ad.getSeller().getId().equals(currentUser.getId()) && !isPrivileged) {
            throw new SecurityException("Access denied");
        }

        return ad;
    }
}

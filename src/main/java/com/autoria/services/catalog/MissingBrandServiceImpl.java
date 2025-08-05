package com.autoria.services.catalog;

import com.autoria.enums.RoleType;
import com.autoria.models.car.dto.MissingBrandRequestDto;
import com.autoria.models.user.AppUser;
import com.autoria.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MissingBrandServiceImpl implements MissingBrandService {

    private final AppUserRepository appUserRepository;

    private final JavaMailSender javaMailSender;

    @Override
    public void reportMissingBrand(MissingBrandRequestDto dto) {
        List<AppUser> managers = appUserRepository.findAllByRoleName(RoleType.MANAGER);

        if (managers.isEmpty()) {
            log.warn("No manager found to notify about missing '{}'", dto.getBrandName());
            return;
        }

        StringBuilder body = new StringBuilder();
        body.append("Reporting a missing car brand/model:\n\n");
        body.append("Brand: ").append(dto.getBrandName()).append("\n");

        if (dto.getModelName() != null && !dto.getModelName().isBlank()) {
            body.append("Model: ").append(dto.getModelName()).append("\n");
        }

        body.append("Email of contact person: ").append(dto.getContactEmail()).append("\n");

        if (dto.getMessage() != null && !dto.getMessage().isBlank()) {
            body.append("Message: ").append(dto.getMessage()).append("\n");
        }

        for (AppUser manager : managers) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(manager.getEmail());
            message.setSubject("Request to add a new car brand/model");
            message.setText(body.toString());
            javaMailSender.send(message);

            log.info("Email sent to manager {} about missing brand '{}'", manager.getEmail(), dto.getBrandName());
        }
    }
}

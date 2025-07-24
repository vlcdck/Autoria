package com.autoria.models.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEmailRequestDto {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @Email
    private String newEmail;

}

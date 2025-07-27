package com.autoria.services.email;

public interface EmailSenderService {
    void sendConfirmationToken(String to, String emailContent);
}

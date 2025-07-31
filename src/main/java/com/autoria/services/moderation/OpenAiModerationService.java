package com.autoria.services.moderation;

import com.autoria.enums.ModerationResult;

public interface OpenAiModerationService {
    ModerationResult checkContent(String content);
}

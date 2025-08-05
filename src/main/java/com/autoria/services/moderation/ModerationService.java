package com.autoria.services.moderation;

import com.autoria.enums.ModerationResult;

public interface ModerationService {
    ModerationResult checkContent(String content);
}

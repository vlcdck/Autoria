package com.autoria.services.moderation;

import com.autoria.enums.ModerationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiModerationServiceImpl implements OpenAiModerationService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ModerationResult checkContent(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, String> body = Map.of("input", content);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/moderations", request, Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("OpenAI returned non-200 status: {}", response.getStatusCode());
                return ModerationResult.ERROR;
            }

            Map<String, Object> firstResult =
                    (Map<String, Object>) ((java.util.List<?>) response.getBody().get("results")).get(0);
            Boolean flagged = (Boolean) firstResult.get("flagged");

            if (flagged != null && flagged) {
                return ModerationResult.FLAGGED;
            } else {
                return ModerationResult.SAFE;
            }

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("OpenAI quota exceeded: {}", e.getMessage());
            return ModerationResult.QUOTA_EXCEEDED;
        } catch (Exception e) {
            log.error("Error calling OpenAI moderation API", e);
            return ModerationResult.ERROR;
        }
    }
}
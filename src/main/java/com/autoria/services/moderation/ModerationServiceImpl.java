package com.autoria.services.moderation;

import com.autoria.enums.ModerationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModerationServiceImpl implements ModerationService {

    @Value("${profanityfilter.api.key}")
    private String profanityApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ModerationResult checkContent(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", profanityApiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://api.api-ninjas.com/v1/profanityfilter")
                .queryParam("text", content)
                .build()
                .encode()
                .toUri();

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("ProfanityFilter API returned non-200 status: {}", response.getStatusCode());
                return ModerationResult.ERROR;
            }

            Map<String, Object> responseBody = response.getBody();
            Boolean isProfanity = (Boolean) responseBody.get("has_profanity");

            if (isProfanity != null && isProfanity) {
                return ModerationResult.FLAGGED;
            } else {
                return ModerationResult.SAFE;
            }

        } catch (Exception e) {
            log.error("Error calling ProfanityFilter API", e);
            return ModerationResult.ERROR;
        }
    }
}
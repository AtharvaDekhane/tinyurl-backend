package com.tinyurl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyurl.dto.UrlClickEvent;
import com.tinyurl.entity.Url;
import com.tinyurl.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UrlRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "url-click-topic", groupId = "tinyurl-group")
    public void consume(String message) {

        try {
            UrlClickEvent event = objectMapper.readValue(message, UrlClickEvent.class);

            Url url = repository.findByShortCode(event.getShortCode()).orElseThrow();
            url.setClickCount(url.getClickCount() + 1);

            repository.save(url);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
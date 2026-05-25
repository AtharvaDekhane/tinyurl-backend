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

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @KafkaListener(
            topics = "url-click-topic",
            groupId = "tinyurl-group"
    )
    public void consume(String message) {

        try {

            System.out.println("MESSAGE RECEIVED: " + message);

            UrlClickEvent event =
                    objectMapper.readValue(
                            message,
                            UrlClickEvent.class
                    );

            System.out.println(
                    "SHORT CODE: " + event.getShortCode()
            );

            Url url = repository.findByShortCode(
                    event.getShortCode()
            ).orElseThrow();

            url.setClickCount(
                    url.getClickCount() + 1
            );

            repository.save(url);

            System.out.println("CLICK COUNT UPDATED");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
package com.tinyurl.service;

import com.tinyurl.dto.UrlClickEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, UrlClickEvent> kafkaTemplate;

    private static final String TOPIC = "url-click-topic";

    public void publishUrlClickEvent(UrlClickEvent event) {

        System.out.println("SENDING EVENT TO KAFKA");

        kafkaTemplate.send(TOPIC, event)
                .whenComplete((result, ex) -> {

                    if (ex == null) {

                        System.out.println(
                                "MESSAGE SENT SUCCESSFULLY"
                        );

                    } else {

                        System.out.println(
                                "FAILED TO SEND MESSAGE"
                        );

                        ex.printStackTrace();
                    }
                });
    }
}
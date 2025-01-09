package com.example.servicecurrency.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CurrencyKafkaService
{
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CurrencyKafkaService(KafkaTemplate<String, String> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCurrencyEvent(String currencyCode)
    {
        kafkaTemplate.send("currency_topic", currencyCode);
    }

    @KafkaListener(topics = "currency_topic", groupId = "exchange_rate_group")
    public void listenCurrencyEvents(String currencyCode)
    {
        // здесь описать процесс работы с пришедшими данными
    }
}

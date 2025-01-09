package com.example.servicecurrency.kafka;

import com.example.servicecurrency.exceptions.customExceptions.NullOrEmptyArgumentException;
import com.example.servicecurrency.service.CurrencyService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer
{
    //region Fields
    private CurrencyService currencyService;
    //endRegion

    //region Constructor
    public TransactionConsumer(CurrencyService currencyService)
    {
        this.currencyService = currencyService;
    }
    //endRegion

    //region Methods
    @KafkaListener(topics = "credit_card_transactions", groupId = "consumer-group-1")
    public void consume(String info)
    {
        if (info == null & info.isEmpty()) // если сообщение пустое или равно нулю, то:
        {
            throw new NullOrEmptyArgumentException("Содержимое из сообщения Kafka равно нулю либо пустое!"); // выбрасываем кастомное исключение, обработанное на глобальном уровне, в случае, если сообщение пустое или равно нулю
        }
            currencyService.returnObjectCurrency(info);
    }
    //endRegion
}

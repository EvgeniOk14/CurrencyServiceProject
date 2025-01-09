package com.example.fetchcurrencyservice.services.savePayLoadAndCurrencyService;

import com.example.fetchcurrencyservice.models.PayloadOfMessage;
import com.example.fetchcurrencyservice.repository.PayloadOfMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

/**
 * Сервис SavePayloadMessageService:
 *
 * Обеспечивает операции по сохранению и управлению полезными нагрузками сообщений.
 * Этот класс взаимодействует с репозиторием для хранения и проверки существования полезных
 * нагрузок, а также для получения и обновления информации о времени последнего сохранения.
 *
 * Поля:
 * - {@link #payloadOfMessageRepository}: Репозиторий для работы с полезными нагрузками сообщений.
 *
 * Конструкторы:
 * - {@link #SavePayloadMessageService(PayloadOfMessageRepository)}:
 *   Конструктор, который инициализирует репозиторий для работы с полезными нагрузками.
 *
 * Методы:
 * - {@link #savePayloadMessage(PayloadOfMessage)}: Сохраняет полезную нагрузку сообщения.
 * - @param payload объект типа PayloadOfMessage, который нужно сохранить.
 *   @return сохраненный объект PayloadOfMessage.
 *
 * - {@link #checkPaylodIfExsist(String)}: Проверяет, существует ли полезная нагрузка в репозитории.
 * - @param currencies строка, представляющая полезную нагрузку для проверки.
 *   @return true, если полезная нагрузка существует, иначе false.
 *
 * - {@link #getLastSavePayload(String)}: Получает дату последнего обновления полезной нагрузки.
 * - @param payLoad строка, представляющая полезную нагрузку для получения даты последнего обновления.
 *   @return дата последнего обновления полезной нагрузки.
 *
 * - {@link #updateLastSavePayload(String, Date)}: Обновляет дату последнего сохранения полезной нагрузки.
 * - @param currency строка, представляющая код валюты, для которой нужно обновить дату.
 * - @param currentDate дата, устанавливаемая как последняя дата сохранения.
 */
@Service
public class SavePayloadMessageService
{
    //region Fields
    private PayloadOfMessageRepository payloadOfMessageRepository;
    //endRegion

    //region Constructor
    public SavePayloadMessageService(PayloadOfMessageRepository payloadOfMessageRepository)
    {
        this.payloadOfMessageRepository = payloadOfMessageRepository;
    }
    //endRegion

    //region Methods
    /**
     * Метод savePayloadMessage:
     * Сохраняет полезную нагрузку сообщения.
     * @param payload - полезная нагрузка сообщения
     *
     * @return PayloadOfMessage - сохраненный объект PayloadOfMessage
     * **/
    public PayloadOfMessage savePayloadMessage(PayloadOfMessage payload)
    {
       return payloadOfMessageRepository.save(payload);
    }

    /**
     * Метод checkPaylodIfExsist:
     * Проверяет, существует ли полезная нагрузка в репозитории
     * @param currencies строка, представляющая полезную нагрузку для проверки.
     * @return true, если полезная нагрузка существует, иначе false.
     * **/
    public boolean checkPaylodIfExsist(String currencies)
    {
       if (payloadOfMessageRepository.existsByPayLoad(currencies))
       {
           return true;
       }
       return false;
    }


    /**
     * Метод getLastSavePayload
     * Получает поле "last_save_payload" т.е. дата последнего обновления
     * @param payLoad строка, представляющая полезную нагрузку для получения даты последнего обновления.
     * @return дата последнего обновления полезной нагрузки.
     *
     * **/
    public Date getLastSavePayload(String payLoad)
    {
        return payloadOfMessageRepository.getLastSavePayloadOfMessageByPayLoad(payLoad);
    }

    /**
     * Метод updateLastSavePayload:
     *  Обновляет дату последнего сохранения полезной нагрузки.
     *  @param currency строка, представляющая код валюты, для которой нужно обновить дату.
     *  @param currentDate дата, устанавливаемая как последняя дата сохранения.
     *
     * **/
    @Transactional
    public void updateLastSavePayload(String currency, Date currentDate)
    {
        payloadOfMessageRepository.updateLastSavePayload(currentDate, currency);
    }
    //endRegion
}



package com.example.requestcurrecyservice.services.kafkaService;

import com.example.requestcurrecyservice.exceptions.customExceptions.*;
import com.example.requestcurrecyservice.repository.PayloadOfMessageRepository;
import com.example.requestcurrecyservice.services.kafkaListenerService.KafkaListenerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;

/**
 * Класс CurrencyKafkaListener:
 *
 * Реализует логику прослушивания сообщений из Kafka-топика "request-currency-topic".
 * Предоставляет методы для обработки входящих сообщений, извлечения необходимых данных и
 * взаимодействия с другими сервисами для получения и сохранения курсов валют.
 *
 * Зависимости:
 * - {@link KafkaListenerService}: сервис для обработки сообщений и взаимодействия с Kafka.
 * - {@link PayloadOfMessageRepository}: репозиторий для доступа к данным сообщений.
 *
 * Поля:
 * - {@link #logger}: Логгер для ведения журнала событий и ошибок.
 * - {@link #kafkaListenerService}: Сервис для обработки сообщений из Kafka.
 * - {@link #payloadOfMessageRepository}: Репозиторий для работы с данными сообщений.
 *
 * Конструкторы:
 * - Конструктор, который инициализирует {@link #kafkaListenerService} и {@link #payloadOfMessageRepository}.
 *
 * Методы:
 * - {@link #listen(ConsumerRecord)}: Слушает топик "request-currency-topic" и обрабатывает входящие сообщения,
 *   извлекая необходимые данные и взаимодействуя с другими сервисами для получения и сохранения курсов валют.
 *
 * - {@link #getLastSavePayload(String)}: Получает дату последнего обновления полезной нагрузки.
 *
 * Исключения:
 * - {@link FailedResponseException}: выбрасывается, если сообщение пустое или равно null.
 * - {@link MassageNotRecognizedException}: выбрасывается, если содержимое сообщения невозможно распознать.
 * - {@link NullOrEmptyArgumentException}: выбрасывается, если полезная нагрузка сообщения равна null или пустая строка.
 *
 * Аннотации:
 * - {@link Service}: указывает, что класс является сервисом и управляется Spring-контейнером.
 * - {@link KafkaListener}: указывает, что метод listen слушает указанный Kafka-топик и обрабатывает входящие сообщения.
 */
@Service
public class CurrencyKafkaListener
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CurrencyKafkaListener.class); // логирование
    private KafkaListenerService kafkaListenerService;
    private PayloadOfMessageRepository payloadOfMessageRepository;
    //endRegion

    //region Constructor
    public CurrencyKafkaListener(KafkaListenerService kafkaListenerService, PayloadOfMessageRepository payloadOfMessageRepository)
    {
        this.kafkaListenerService = kafkaListenerService;
        this.payloadOfMessageRepository = payloadOfMessageRepository;
    }
    //endRegion

    //region Methods
    /**
     * Метод listen:
     * Описание метода: слушает топик "fetch-currency-topic" и принимает из него входящие сообщения.
     * Передаёт их в соответствующие методы для получения курсов валют со стороннего API.
     *
     * @param record - сообщение из Kafka, топика "fetch-currency-topic".
     * @throws FailedResponseException       - выбрасывается, если сообщение пустое или равно null.
     * @throws MassageNotRecognizedException - выбрасывается, если содержимое сообщения невозможно распознать.
     *
     */
    @KafkaListener(topics = "request-currency-topic", groupId = "currency-service")
    public void listen(ConsumerRecord<String, String> record)
    {
        if (record == null)
        {
            logger.error("Получено сообщение С заголовком: {} и полезной нагрузкой {} равное null", record.key(), record.value());
            throw new FailedResponseException("Получено сообщение равное null");
        }
        System.out.println("Cлушаем топик: request-currency-topic  и приняли сообщение: " + record.value()); // вывод для наглядности в консоль

        try
        {
            Map<String, String> headersMap = kafkaListenerService.extractHeaders(record); // извлекаем параметры заголовка и кладём их в Map()

            String requestId = headersMap.get("kafka_messageKey"); // получаем из заголовка поле requestId
            System.out.println("Извлекли requestId: " + requestId); // печатаем для наглядности

            String payload = record.value(); // полезная нагрузка (например: "ALL:" или "SINGLE:{USD}", или "FILTER:{USD, EUR, RUB}")
            System.out.println("печатаем из заголовка полезную нагрузку payload: " + payload);

            // обрабатываем случай когда полезная нагрузка сообщения равна null или пустая строка:
            if (payload == null ||  payload.isEmpty()) // проверяем полезную нагрузку из сообщения на null
            {
                logger.error("Ошибка: Пришло пустое сообщение! C requestId = " + requestId); // логируем ошибку
                throw new NullOrEmptyArgumentException("Ошибка: Пришло пустое сообщение! C requestId =  " + requestId); // обрабатываем исключение
            }

            // обрабатываем случай когда уникальный идентификатор запроса сообщения равен null
            if (requestId == null) // если requestId равен null
            {
                logger.warn("requestId {} отсутствует в заголовках сообщения: ", requestId); // логируем предупреждение
                System.out.println("requestId отсутствует в заголовках сообщения"); // вывод для наглядности в консоль
            }


                // проверяем актуальность сохранённых данных в базе данных (если last_sava_payload меньше 1 часа) т.е. данные от стороннего API, т.е. предполагаем, что сохраненные данные полученные от стороннего API актуальны в течении одного часа, а затем курсы валют могут поменяться на сайте
                Date lastSavePayload = getLastSavePayload(payload); // получаем время последнего сохранения по заданным параметрам запроса
                System.out.println("Выводим для проверки lastSavePayload: " + lastSavePayload); // Вывод в терминал для наглядности


                // Извлекаем extractCurrencyType и extractCurrencyCodeFromMessage ..............:
                //String extractCurrencyType = kafkaListenerService.extractMessageType(payload); // извлекаем из полезной нагрузки тип сообщения ("ALL:" или "SINGLE:" или "FILTER:")
                String extractCurrencyCodeFromMessage = kafkaListenerService.extractCurrencyCode(payload); // извлекаем из полезной нагрузки коды валют {USD, EUR, RUB ... и т.д.}
                System.out.println("!!!!!!!!!!!! extractCurrencyCodeFromMessage: " + extractCurrencyCodeFromMessage + "!!!!!!!!!!!!!!!!!!!!");

                // проверяем данные на их актуальность (больше одного часа или нет)
                // если да, то берем новые данные от стороннего API и обновляем данные в базе данных
                //если нет, то берём данные из базы данных
                kafkaListenerService.checkLastSavePayload(lastSavePayload, payload, requestId, extractCurrencyCodeFromMessage);

        }
        catch (MassageNotRecognizedException ex)
        {
            logger.error("Ошибка при обработке сообщения", ex);
            kafkaListenerService.sendToDLT(record, ex.getMessage());  // Отправка сообщения в Dead Letter Topic
        }
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
    //endRegion
}







package com.example.fetchcurrencyservice.services.kafkaService;

import com.example.fetchcurrencyservice.exceptions.customExceptions.*;
import com.example.fetchcurrencyservice.models.ExchangeRateResponseFromAPI;
import com.example.fetchcurrencyservice.services.currencyService.CurrencyService;
import com.example.fetchcurrencyservice.services.kafkaListenerService.KafkaListenerService;
import com.example.fetchcurrencyservice.services.requestIdService.RequestIdService;
import com.example.fetchcurrencyservice.services.savePayLoadAndCurrencyService.SavePayloadMessageService;
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
 * Реализует логику прослушивания сообщений из Kafka-топика "fetch-currency-topic".
 * Предоставляет методы для обработки входящих сообщений, извлечения необходимых данных и
 * взаимодействия с другими сервисами для получения и сохранения курсов валют.
 *
 * Зависимости:
 * - kafkaListenerService: сервис для обработки сообщений и взаимодействия с Kafka.
 * - requestIdService: сервис для управления уникальными идентификаторами запросов.
 * - savePayloadMessageService: сервис для сохранения полезных нагрузок сообщений в базе данных.
 *
 * Поля:
 * - {@link #logger}: Логгер для ведения журнала событий и ошибок.
 * - {@link #kafkaListenerService}: Сервис для обработки сообщений из Kafka.
 * - {@link #requestIdService}: Сервис для работы с уникальными идентификаторами запросов.
 * - {@link #savePayloadMessageService}: Сервис для сохранения полезных нагрузок сообщений.
 *
 * Конструкторы:
 * - {@link #CurrencyKafkaListener(KafkaListenerService, RequestIdService, SavePayloadMessageService, CurrencyService currencyService)}:
 *   Конструктор, который инициализирует {@link #kafkaListenerService}, {@link #requestIdService}, {@link #currencyService}
 *   и {@link #savePayloadMessageService}.
 *
 * Методы:
 * - {@link #listen(ConsumerRecord)}: Слушает топик "fetch-currency-topic" и обрабатывает входящие сообщения,
 *   извлекая необходимые данные и взаимодействуя с другими сервисами для получения и сохранения курсов валют.
 *
 * Исключения:
 * - {@link FailedResponseException}: выбрасывается, если сообщение пустое или равно null.
 * - {@link MassageNotRecognizedException}: выбрасывается, если содержимое сообщения невозможно распознать.
 * - {@link NullOrEmptyArgumentException}: выбрасывается, если полезная нагрузка сообщения равна null или пустая строка.
 *
 * Аннотации:
 * - @Service: указывает, что класс является сервисом и управляется Spring-контейнером.
 * - @KafkaListener: указывает, что метод listen слушает указанный Kafka-топик и обрабатывает входящие сообщения.
 */

@Service
public class CurrencyKafkaListener
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CurrencyKafkaListener.class); // логирование
    private KafkaListenerService kafkaListenerService;
    private RequestIdService requestIdService;
    private SavePayloadMessageService savePayloadMessageService;
    private CurrencyService currencyService;
    //endRegion

    //region Constructor
    public CurrencyKafkaListener(KafkaListenerService kafkaListenerService, RequestIdService requestIdService,
                                 SavePayloadMessageService savePayloadMessageService,
                                 CurrencyService currencyService)
    {
        this.kafkaListenerService = kafkaListenerService;
        this.requestIdService = requestIdService;
        this.savePayloadMessageService = savePayloadMessageService;
        this.currencyService = currencyService;
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
    @KafkaListener(topics = "fetch-currency-topic", groupId = "currency-service")
    public void listen(ConsumerRecord<String, String> record)
    {
        if (record == null)
        {
            logger.error("Получено сообщение С заголовком: {} и полезной нагрузкой {} равное null", record.key(), record.value());
            throw new FailedResponseException("Получено сообщение равное null");
        }
        System.out.println("Cлушаем топик: fetch-currency-topic  и приняли сообщение: " + record.value()); // вывод для наглядности в консоль

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


            //  случай когда запрос с таким requestId уже был обработан ранее:
            if (requestIdService.isRequestIdExist(requestId)) // если true, т.е. такой запрос с номер requestId уже был обработан ранее, то:
            {
                System.out.println("такой запрос с номер requestId уже был обработан ранее! "); // вывод для наглядности в консоль
                logger.warn("такой запрос с номером requestId {} уже был обработан ранее!", requestId); // логируем предупреждение информацию
                return; // игнорируем запрос, т.к. он является дублем и выходим (прекращаем обрабатывать этот запрос!)
            }
            // случай когда запрос с таким requestId приходит в первые:
            else // если false, т.е. такой запрос с номер requestId не был обработан ранее, то:
            {
                kafkaListenerService.saveRequestId(requestId); //  Сохраняет requestId и expirationDate в базе данных MongoDB, в коллекции requestId-collection

                //  Проверяем, был ли уже запрос ранее с такой полезной нагрузкой payload? (т.е. payload сохранён в базе данных в таблице "payload_table" ?):

                if (!savePayloadMessageService.checkPaylodIfExsist(payload)) // проверяем, сохранён ли был ранее такой запрос в базе данных, если false (не был ранее сохранён)
                {
                    // случай 1 - false, если полезная нагрузка payload НЕ БЫЛА сохранена ранее в базе данных:

                    // проверяем переданные коды валют на доступность, т.е. присутствие их на сайте стороннего API:
                    if (!currencyService.isSentCurrenciesValid(kafkaListenerService.extractCurrencyCode(payload))) // false, такие коды валют не доступны на сайте
                    {
                        System.out.println("Вы передали коды валют которые отсутствуют в списке доступных на стороннем API");
                        logger.info("Вы передали коды валют которые отсутствуют в списке доступных на стороннем API");
                        return;
                    }
                }
                if (savePayloadMessageService.checkPaylodIfExsist(payload))  // случай 2 - true, если полезная нагрузка payload УЖЕ БЫЛА сохранена ранее в базе данных, то:
                {
                    // проверяем актуальность сохранённых данных в базе данных (если last_sava_payload меньше 1 часа) т.е. данные от стороннего API, т.е. предполагаем, что сохраненные данные полученные от стороннего API актуальны в течении одного часа, а затем курсы валют могут поменяться на сайте
                    Date lastSavePayload = savePayloadMessageService.getLastSavePayload(payload); // получаем время последнего сохранения по заданным параметрам запроса
                    System.out.println("Выводим для проверки lastSavePayload: " + lastSavePayload); // Вывод в терминал для наглядности

                    // Извлекаем extractCurrencyType и extractCurrencyCodeFromMessage ..............:
                    //String extractCurrencyType = kafkaListenerService.extractMessageType(payload); // извлекаем из полезной нагрузки тип сообщения ("ALL:" или "SINGLE:" или "FILTER:")
                    String extractCurrencyCodeFromMessage = kafkaListenerService.extractCurrencyCode(payload); // извлекаем из полезной нагрузки коды валют {USD, EUR, RUB ... и т.д.}


                    // проверяем данные на их актуальность (больше одного часа или нет)
                    // если да, то берем новые данные от стороннего API и обновляем данные в базе данных
                    //если нет, то берём данные из базы данных
                    kafkaListenerService.checkLastSavePayload(lastSavePayload, payload, requestId, extractCurrencyCodeFromMessage);
                    return;
                }
            }

            // Случай кода запрос с такой полезной нагрузкой пришёл в первые. Основная логика обработки сообщения:
            System.out.println("начинаем логику обработки сообщения пришедшего впервые..................");

            kafkaListenerService.savePayloadInDb(payload); // Сохранение полезной нагрузки сообщения payload в базе данных PostgreSQL в таблице payload_table

            // Извлечение и проверка кода валюты из полезной нагрузки:
            String currencyCode = kafkaListenerService.extractCurrencyCode(payload);

            // В случае валидности переданных кодов валют, обработка данных валюты:
            kafkaListenerService.processCurrencyData(currencyCode, requestId); // вызов метода, обрабатывающего сообщения по кодам валют и идентификатору запроса
        }
        catch (MassageNotRecognizedException ex)
        {
            logger.error("Ошибка при обработке сообщения", ex);
            kafkaListenerService.sendToDLT(record, ex.getMessage());  // Отправка сообщения в Dead Letter Topic
        }
    }
    //endRegion
}







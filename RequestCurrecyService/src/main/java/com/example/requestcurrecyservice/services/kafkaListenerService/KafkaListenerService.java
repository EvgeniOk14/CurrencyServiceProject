package com.example.requestcurrecyservice.services.kafkaListenerService;

import com.example.requestcurrecyservice.exceptions.customExceptions.*;
import com.example.requestcurrecyservice.models.ResponseFutureManager;
import com.example.requestcurrecyservice.models.ResponseToKafkaServer;
import com.example.requestcurrecyservice.repository.ResponseToKafkaServerepository;
import com.example.requestcurrecyservice.services.currencyService.CurrencyService;
import com.example.requestcurrecyservice.services.kafkaService.CurrencyKafkaListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Класс KafkaListenerService:
 *
 * Предоставляет методы для обработки сообщений из Kafka, взаимодействия с базой данных и получения данных о курсах валют.
 * Обеспечивает логику валидации, сериализации и отправки сообщений в топики Kafka, а также взаимодействия с
 * сервисами для получения актуальных данных о курсах валют.
 *
 * Зависимости:
 * - {@link KafkaTemplate}: шаблон для работы с Kafka, используемый для отправки сообщений.
 * - {@link CurrencyService}: сервис для работы с курсами валют.
 * - {@link ResponseToKafkaServerepository}: репозиторий для доступа к ответам сервера Kafka.
 * - {@link ResponseFutureManager}: менеджер для обработки асинхронных ответов.
 *
 * Поля:
 * - {@link #logger}: Логгер для ведения журнала событий и ошибок.
 * - {@link #kafkaTemplate}: Шаблон для работы с Kafka.
 * - {@link #currencyService}: Сервис для работы с курсами валют.
 * - {@link #responseToKafkaServerepository}: Репозиторий для доступа к данным ответов.
 * - {@link #responseFutureManager}: Менеджер для обработки асинхронных ответов.
 *
 * Конструкторы:
 * - Конструктор, который инициализирует все зависимости.
 *
 * Методы:
 * - {@link #checkLastSavePayload(Date, String, String, String)}: Проверяет актуальность данных о курсах валют
 *   и обновляет их при необходимости, отправляя сообщения в соответствующие топики.
 *
 * - {@link #getDataFromDB(String, String, String)}: Извлекает данные из базы данных и отправляет их в указанный топик.
 *
 * - {@link #doesResponseExist(String)}: Проверяет наличие записи с указанной валютой в базе данных.
 *
 * - {@link #extractHeaders(ConsumerRecord)}: Извлекает заголовки из записи Kafka и преобразует их в карту.
 *
 * - {@link #extractCurrencyCode(String)}: Извлекает код валюты из переданного сообщения.
 *
 * - {@link #serializeToJson(ResponseToKafkaServer)}: Сериализует объект ResponseToKafkaServer в JSON-строку.
 *
 * - {@link #askFetchCurrencyServiceToGetAndUpdateandSendData(String, String)}: Запрашивает обновление данных о курсах валют.
 *
 * - {@link #sendToKafka(String, String, String)}: Отправляет сообщение в указанный топик Kafka.
 *
 * - {@link #sendToDLT(ConsumerRecord, String)}: Отправляет сообщение в тему мертвых писем (Dead Letter Topic, DLT).
 *
 * Исключения:
 * - {@link NullOrEmptyArgumentException}: выбрасывается, если передан пустой или равный null аргумент.
 * - {@link NotFoundObjectInDbException}: выбрасывается, если объект не найден в базе данных.
 * - {@link FailConnectException}: выбрасывается, если произошла ошибка при подключении к базе данных.
 * - {@link FaileMappinhToJSON}: выбрасывается, если произошла ошибка при сериализации объекта в JSON.
 *
 * Аннотации:
 * - {@link Service}: указывает, что класс является сервисом и управляется Spring-контейнером.
 * - {@link Transactional}: указывает, что методы должны выполняться в рамках транзакции.
 */
@Service
public class KafkaListenerService
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CurrencyKafkaListener.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private CurrencyService currencyService;
    private ResponseToKafkaServerepository responseToKafkaServerepository;

    private ResponseFutureManager responseFutureManager;
    //endRegion

    //region Constructor
    public KafkaListenerService(KafkaTemplate<String, String> kafkaTemplate, CurrencyService currencyService,
                                 ResponseToKafkaServerepository responseToKafkaServerepository, ResponseFutureManager responseFutureManager)
    {
        this.kafkaTemplate = kafkaTemplate;
        this.currencyService = currencyService;
        this.responseToKafkaServerepository = responseToKafkaServerepository;
        this.responseFutureManager = responseFutureManager;
    }
    //endRegion

    //region Methods
    /**
     * Метод checkLastSavePayload:
     * Предназначен для проверки актуальности данных по курсам валют сохранённых в базе данных
     * Если данным больше либо равны одному часу, то: берём данные от стороннего API и обновляем данные в базе данных
     * Если данным меньше одного часа, то: берём данные из базы данных и отсылаем их в топик
     *
     * @param lastSavePayload - дата последнего сохранения полезной нагрузки currency в базе данных PostgreSQL таблице "pay_load_table"
     *
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * **/
    @Transactional
    public void checkLastSavePayload(Date lastSavePayload, String payload, String requestId, String extractCurrencyFromMessage)
    {
        if (lastSavePayload == null) // если такого запроса ещё не было, то:
        {
            logger.error("lastSavePayload {} не сохранялся в базе данных MongoDB, перенаправляем запрос в микросервис FetchCurrencyService", lastSavePayload); // логируем данное сообщение
            // говорим микросервису FetchCurrencyService взять данные от стороннего API и обновить их в базе данных, подготовить ответ, а затем отправить его в топик "response-topic" в микросервис KafkaServer:
            askFetchCurrencyServiceToGetAndUpdateandSendData(payload, requestId);
            return;
        }
        if (requestId == null || requestId.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент{}", requestId);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        if (extractCurrencyFromMessage == null || extractCurrencyFromMessage.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент{}", extractCurrencyFromMessage);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        try
        {
            // Получаем текущее время
            LocalDateTime now = LocalDateTime.now();

            // Преобразуем Date в LocalDateTime
            LocalDateTime lastSaveLocalDateTime = lastSavePayload.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            System.out.println("Печать времени последнего сохранения lastSaveLocalDateTime: " + lastSaveLocalDateTime);

            // Вычисляем разницу во времени
            Duration duration = Duration.between(lastSaveLocalDateTime, now); // duration - разница во времени между текущей датаой и последним сохранением в базе данных

            // Время меньше 1 часа
            System.out.println("Время меньше 1 часа. Выполняем действия: берём данные из базы данных и отправляем их в топик");

            getDataFromDB(extractCurrencyFromMessage, requestId, payload);  // берём данные из базы данных (это  и будет ответом) и отправляем их в топик "response-topic" в микросервис KafkaServer


            // Проверяем, больше ли разница 1 часа
            if (duration.toHours() >= 1)
            {
                // Время больше или равно 1 часу
                System.out.println("Время больше или равно 1 часа. Данные устарели! Выполняем действия: запрос актуальных данных от стороннего API и отправка их в топик");

                // говорим микросервису FetchCurrencyService взять данные от стороннего API и обновить их в базе данных, подготовить ответ, а затем отправить его в топик "response-topic" в микросервис KafkaServer:
                askFetchCurrencyServiceToGetAndUpdateandSendData(payload, requestId);
            }
        }
        catch (NullOrEmptyArgumentException ex)
        {
            logger.error("передан аргумент равный null{} ", ex.getMessage());
            throw ex;
        }
    }


    /**
     * Метод getDataFromDB:
     * Извлекает данные из базы данных на основе переданных параметров.
     * Проверяет, существуют ли данные в базе, и если да,
     * получает объект ответа и отправляет его в указанный топик Kafka в формате JSON.
     * если нет, то пересылает сообщение в топик "fetch-currency-service" в микросервис FetchCurrencyService, чтобы он обновил данные и отправил их в микросервис KafkaServer
     *
     * @param extractCurrencyFromMessage валюта, извлекаемая из сообщения.
     * @param requestId уникальный идентификатор запроса, используемый в качестве ключа для сообщения.
     *
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * @exception
     * @exception NotFoundObjectInDbException - при проблеме получения данных из базы данных
     * @exception FailConnectException - при проблеме с соединением с базой данных
     * @exception FaileMappinhToJSON - при проблеме трансформации объекта в Json
     */
    public void getDataFromDB(String extractCurrencyFromMessage, String requestId, String payload)
    {
        if (extractCurrencyFromMessage == null || extractCurrencyFromMessage.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", extractCurrencyFromMessage);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        if (requestId == null || requestId.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", requestId);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            // Проверяем, существует ли ответ в базе данных
            if (doesResponseExist(extractCurrencyFromMessage)) // проверяем, если ответ true, т.е. такие данные есть в базе данных, т.е. true, то:
            {
                // Получаем заполненный объект ResponseToKafkaServer из базы данных "response_to_kafka"

                ResponseToKafkaServer responseToKafkaServer = responseToKafkaServerepository.findByCurrencyWithRates(extractCurrencyFromMessage); // получаем коды валют и их курсы из базы данных

                String responseMessageToTopic = serializeToJson(responseToKafkaServer); // Сериализуем ответ в JSON

                sendToKafka(requestId, responseMessageToTopic, "response-topic"); // Отправляем ответ с  данными в топик "response-topic" в микросервис KafkaServer

                // Логируем информацию об отправке данных
                System.out.println("микросервис ResponseCurrencyService подготовил ответ, взяв его из базы данных и отправил ответ в топик!"); // Логируем информацию об отправке данных
                logger.info("Ответ подготовил - микросервис ResponseCurrencyService. Ответ: {} взят из базы данных и отправлен в топик!", responseToKafkaServer); // логируем информацию об успешной отправки ответа в топик
            }
            else
            {
                // если не совпадают коды валют по количеству или по факту, т.е. false, то
                askFetchCurrencyServiceToGetAndUpdateandSendData(payload, requestId);
            }
        }
        catch (NotFoundObjectInDbException ex)
        {
            logger.error("Невозможность нахождения данных в базе данных");
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("Невозможность соединения с базой данных");
            throw  ex;
        }
        catch (FaileMappinhToJSON ex)
        {
            logger.error("Невозможность трансформации объекта в Json");
            throw ex;
        }

    }


    /**
     * Метод doesResponseExist:
     * Проверяет, существует ли запись с указанной валютой в базе данных.
     *
     * Этот метод принимает строку, представляющую валюту, и проверяет наличие соответствующей записи
     * в репозитории. Если запись существует, метод также сравнивает коды валют и их обменные курсы,
     * чтобы определить, совпадают ли данные с запрашиваемыми.
     *
     * @param extractCurrencyFromMessage строка, представляющая коды валют для проверки.
     *                                    Не должна быть null или пустой строкой.
     * @return true, если запись существует и данные совпадают, иначе false.
     * @throws NullOrEmptyArgumentException если передан аргумент, равный null или пустой строке.
     * @throws NotFoundObjectInDbException если объект не найден в базе данных.
     * @throws FailConnectException если произошла ошибка при подключении к базе данных.
     */
    public boolean doesResponseExist(String extractCurrencyFromMessage)
    {
        if (extractCurrencyFromMessage == null || extractCurrencyFromMessage.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент!");
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        try
        {
            // Проверяем наличие записи с указанной валютой
            //ResponseToKafkaServer response = responseToKafkaServerepository.findByCurrency(currency);
            ResponseToKafkaServer response = responseToKafkaServerepository.findByCurrencyWithRates(extractCurrencyFromMessage);
            if (response == null)
            {
                logger.error("Объект не найден в базе данных");
                throw new NotFoundObjectInDbException("Объект не найден в базе данных");
            }

            String extractedCurrency = response.getCurrency(); // извлекаем коды валют
            Map<String, Double> extractedMap = response.getRates(); // создаём коллекцию Map<String, Double> с ключём - код валюты и значением - обменным курсом валюты

            String extractedDate = response.getDate(); // получаем дату, на момент которой были утановлены курсы валют
            String extractedBaseCurrency = response.getBaseCurrency(); // получаем базовую валюту, относительно которой устанавливают обменные курсы всех валют
            String extractedRequestId = response.getRequestId(); // получаем уникальный идентификационный номер запроса requestId

            System.out.println("");
            System.out.println("!!!!!!!!!!!__________________________________________!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("Печатаем - extractedCurrency: " + extractedCurrency);
            System.out.println("Печатаем - extractedMap: " + extractedMap);
            System.out.println("Печатаем - extractedDate: " + extractedDate);
            System.out.println("Печатаем - extractedBaseCurrency: " + extractedBaseCurrency);
            System.out.println("Печатаем - extractedRequestId: " + extractedRequestId);
            System.out.println("!!!!!!!!!!!__________________________________________!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("");


            // Создаем массив валют из входного сообщения
            String[] rateArray = extractCurrencyFromMessage.trim().split(",\\s*"); // Убираем пробелы
            Set<String> rateSet = new HashSet<>(Set.of(rateArray)); // Создаем набор уникальных валют

            // Разбиваем extractedCurrency на массив и создаем Set для уникальных валют
            String[] extractedCurrencyArray = extractedCurrency.trim().split(",\\s*"); // Убираем пробелы
            Set<String> extractedCurrencySet = new HashSet<>(Set.of(extractedCurrencyArray)); // Создаем набор уникальных валют

            if (!(extractedCurrencySet.size() == rateSet.size())) // если длины массивов уникальных значений кодов валют не совпадает, т.е. курсов валют больше в одном из списков, то false:
            {
                return false; // количество курсов валют не совпало (т.е. данные нужно запрашивать от стороннего API)
            }

            for (String code : rateSet) // итерируемся по коллекции
            {
                if (!extractedMap.containsKey(code)) // смотрим, содержит ли коллекция код валюты из переданного сообщения
                {
                    return false; // курсы валют в списке не совпали (т.е. данные нужно запрашивать от стороннего API)
                }
            }
            return true; // Все коды валют совпали,как по количеству, так и по самим кодам валют (т.е. на данный запрос уже сохранён ответ в базе данных и нужно возвращать ответ из базы данных)
        }
        catch (NotFoundObjectInDbException ex)
        {
            logger.error("невозможно найти объект в базе данных");
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("невозможно установить связь с базой данных");
            throw ex;
        }
    }


    /**
     * Метод extractHeaders:
     * Извлекает заголовки из записи Kafka и преобразует их в карту.
     * Заголовки представляют собой пары ключ-значение, где ключи являются именами заголовков,
     * а значения - их содержимым. Метод возвращает карту, содержащую все заголовки.
     *
     * @param record запись Kafka, содержащая заголовки, которые необходимо извлечь.
     * @return возвращает карту, где ключами являются имена заголовков, а значениями - их содержимое.
     * @exception NullOrEmptyArgumentException если переданный аргумент record равен null.
     */
    public Map<String, String> extractHeaders(ConsumerRecord<String, String> record)
    {
        if (record == null)
        {
            logger.error("Передан пустой или равный nul аргумент {}", record);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        // Создаем новую карту для хранения заголовков
        Map<String, String> headersMap = new HashMap<>();

        // Перебираем все заголовки в записи Kafka
        for (Header header : record.headers())
        {
            // Получаем имя заголовка
            String headerName = header.key();

            // Получаем значение заголовка и преобразуем его в строку с использованием кодировки UTF-8
            String headerValue = new String(header.value(), StandardCharsets.UTF_8);

            // Добавляем заголовок в карту
            headersMap.put(headerName, headerValue); // Добавление заголовка в карту
        }

        // Возвращаем карту с заголовками
        return headersMap;
    }


    /**
     * Метод extractCurrencyCode:
     * Извлекает код валюты из переданного сообщения.
     * Метод проверяет, начинается ли сообщение с определенных префиксов
     * и возвращает соответствующий код валюты или выбрасывает исключение,
     * если сообщение не соответствует ожидаемому формату.
     *
     * @param payload полезная нагрузка входящего сообщения, из которого необходимо извлечь код валюты.
     * @return код валюты, извлеченный из сообщения.
     * @throws MassageNotRecognizedException если сообщение не содержит кода валюты или не распознано.
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     */
    @Transactional
    public String extractCurrencyCode(String payload)
    {
        System.out.println("передали в метод payload: "  + payload);

        if (payload == null || payload.trim().isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", payload);
            System.out.println("Проверка на ноль payload!!!!!!!!! " + payload);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        // Проверяем, начинается ли сообщение с "FILTER:" или "SINGLE:"
        if (payload.startsWith("FILTER:") || payload.startsWith("SINGLE:"))
        {
            String[] parts = payload.split(":");

            // Проверяем, что в массиве есть как минимум два элемента
            if (parts.length > 1)
            {
                return parts[1].trim(); // Убираем лишние пробелы и возвращаем код валюты
            }
            else
            {
                logger.error("Сообщение {} не содержит кода валюты. ", payload);
                // Выбрасываем исключение, если код валюты отсутствует
                throw new MassageNotRecognizedException("Сообщение не содержит кода валюты: " + payload);
            }
        }
        else if (payload.startsWith("ALL:"))
        {
            // Если сообщение начинается с "ALL:", возвращаем специальный код
            return "ALL"; // Возвращаем ALL
        }
        else
        {
            logger.error("Сообщение{} не распознано: ", payload);
            // Выбрасываем исключение, если сообщение не распознано
            throw new MassageNotRecognizedException("Сообщение не распознано: " + payload);
        }
    }



    /**
     * Метод serializeToJson:
     * Предназначен для сериализации объекта ResponseToKafkaServer в JSON-строку.
     *
     * @param response Объект ResponseToKafkaServer, который нужно сериализовать.
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * @return JSON-строка или null в случае ошибки.
     */
    @Transactional
    public String serializeToJson(ResponseToKafkaServer response)
    {
        if (response == null)
        {
            logger.error("Передан пустой или равный nul аргумент {}", response);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            return objectMapper.writeValueAsString(response); // Преобразуем объект в JSON-строку
        }
        catch (JsonProcessingException e)
        {
            System.err.println("Ошибка сериализации объекта: " + e.getMessage());
            logger.error("Ошибка сериализации объекта: {} ", e.getMessage());
            return null; // Возвращаем null в случае ошибки
        }
    }


    /**
     * Метод askFetchCurrencyServiceToGetAndUpdateandSendData:
     * Запрашивает обновление данных о курсах валют и отправляет сообщение в соответствующий топик.
     *
     * @param payload Полезная нагрузка сообщения.
     * @param requestId Уникальный идентификатор запроса.
     */
    public void askFetchCurrencyServiceToGetAndUpdateandSendData(String payload, String requestId)
    {
        sendToKafka(requestId,payload, "fetch-currency-topic");

    }


    /**
     * Метод sendToKafka:
     * Отправляет сообщение в указанный топик Kafka с заданным ключом и полезной нагрузкой.
     * Сообщение оборачивается в ProducerRecord и может содержать дополнительные заголовки.
     * Метод выполняет отправку в рамках транзакции, обеспечивая атомарность операции.
     *
     * @param requestId уникальный идентификатор запроса, используемый в качестве ключа для сообщения.
     * @param responseMessageToTopic сообщение, которое будет отправлено в топик.
     * @exception NullOrEmptyArgumentException - при проблеме с передачей пустого аргумента
     * @exception RuntimeException если возникает ошибка при отправке сообщения в Kafka.
     */
    public void sendToKafka(String requestId, String responseMessageToTopic, String topicName)
    {
        if (responseMessageToTopic == null)
        {
            logger.error("Передан пустой или равный nul аргумент {}", responseMessageToTopic);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        if (requestId == null || requestId.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", requestId);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            // Создаем ProducerRecord для отправки в Kafka
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                    topicName, // Название топика
                    requestId, // Ключ (можно указать null, если ключ не нужен)
                    responseMessageToTopic // Полезная нагрузка
            );

            // Добавляем заголовки, если необходимо
            producerRecord.headers().add("kafka_messageKey", requestId.getBytes(StandardCharsets.UTF_8));
            producerRecord.headers().add("kafka_correlationId", requestId.getBytes(StandardCharsets.UTF_8));
            producerRecord.headers().add("source_service", "RequestCurrencyService".getBytes(StandardCharsets.UTF_8));


            // Отправляем сообщение в Kafka в рамках транзакции
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try
                {
                    // Выполняем отправку сообщения в рамках транзакции
                    kafkaTemplate.executeInTransaction(operations -> {
                        operations.send(producerRecord); // Отправка ProducerRecord
                        return null; // Возвращаем null, так как ничего не нужно возвращать
                    });
                    // Логируем успешную отправку сообщения
                    logger.info("Ответ с заголовком kafka_messageKey отправлен в Kafka: {}", responseMessageToTopic);
                }
                catch (Exception e)
                {
                    // Логируем ошибку при отправке сообщения
                    logger.error("Ошибка при отправке ответа с заголовком kafka_messageKey", e);
                    throw new RuntimeException("Не удалось отправить ответ в Kafka: " + e.getMessage());
                }
            });

            // Обработка завершения CompletableFuture (если требуется)
            future.whenComplete((result, throwable) -> {
                if (throwable != null)
                {
                    // Логируем ошибку при завершении отправки сообщения в Kafka
                    logger.error("Ошибка при завершении отправки сообщения в Kafka", throwable);
                }
                else
                {
                    // Логируем успешное завершение отправки сообщения
                    logger.info("Сообщение успешно отправлено в Kafka");
                }
            });

        }
        catch (KafkaException ex)
        {
            logger.error("Проблемы с передачей сообщения в топик кафка");
            throw ex;
        }
    }



    /**
     * Метод sendToDLT:
     * Отправляет сообщение в тему мертвых писем (Dead Letter Topic, DLT)
     * с указанием причины отправки. Если отправка сообщения не удалась,
     * будет зафиксирована ошибка в логах.
     *
     * @param record объект ConsumerRecord, содержащий ключ и значение сообщения,
     *               которое необходимо отправить в DLT.
     * @param reason причина, по которой сообщение отправляется в DLT.
     *
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * @exception KafkaException - при проблеме с отправкой сообщения в кафка
     */
    @Transactional
    public void sendToDLT(ConsumerRecord<String, String> record, String reason)
    {
        if (record == null)
        {
            logger.error("Передан пустой или равный nul аргумент {}", record);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        if (reason == null)
        {
            logger.error("Передан пустой или равный nul аргумент {}", reason);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            // Отправляем сообщение в тему мертвых писем (DLT)
            kafkaTemplate.send("dead-letter-topic", record.key(), "Reason: " + reason + ", Message: " + record.value());

            // Логируем успешную отправку сообщения в DLT
            logger.info("Сообщение отправлено в DLT: {}", reason);
        }
        catch (KafkaException ex)
        {
            // Логируем ошибку, если отправка сообщения в DLT не удалась
            logger.error("Не удалось отправить сообщение в DLT.", ex);
            throw ex;
        }
    }
    //endRegion
}

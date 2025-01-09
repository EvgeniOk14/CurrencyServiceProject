package com.example.fetchcurrencyservice.services.kafkaListenerService;

import com.example.fetchcurrencyservice.exceptions.customExceptions.*;
import com.example.fetchcurrencyservice.models.PayloadOfMessage;
import com.example.fetchcurrencyservice.models.ResponseToKafkaServer;
import com.example.fetchcurrencyservice.repository.ResponseToKafkaServerepository;
import com.example.fetchcurrencyservice.services.currencyService.CurrencyService;
import com.example.fetchcurrencyservice.services.kafkaService.CurrencyKafkaListener;
import com.example.fetchcurrencyservice.services.requestIdService.RequestIdService;
import com.example.fetchcurrencyservice.services.savePayLoadAndCurrencyService.SavePayloadMessageService;
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
 * Реализует бизнес-логику для обработки сообщений из Kafka, включая сохранение и обновление данных о валютах,
 * управление запросами и взаимодействие с базой данных. Обрабатывает запросы на получение курсов валют,
 * проверяет их валидность и отправляет результаты обратно в Kafka.
 *
 * Зависимости:
 * - KafkaTemplate: шаблон для работы с Kafka, используемый для отправки сообщений.
 * - CurrencyService: сервис для получения курсов валют от стороннего API.
 * - RequestIdService: сервис для управления идентификаторами запросов.
 * - SavePayloadMessageService: сервис для сохранения полезной нагрузки сообщений в базе данных.
 * - ResponseToKafkaServerepository: репозиторий для работы с данными о курсах валют в базе данных.
 *
 * Методы:
 * - {@link #saveRequestId(String)}: Сохраняет уникальный идентификатор запроса и дату истечения в базе данных.
 * - {@link #savePayloadInDb(String)}: Сохраняет полезную нагрузку сообщения в базе данных PostgreSQL.
 * - {@link #updatePayloadInDb(String)}: Обновляет дату последнего сохранения полезной нагрузки в базе данных.
 * - {@link #extractHeaders(ConsumerRecord)}: Извлекает заголовки из записи Kafka и преобразует их в карту.
 * - {@link #sendToDLT(ConsumerRecord, String)}: Отправляет сообщение в тему мертвых писем (DLT) с указанием причины.
 * - {@link #extractMessageType(String)}: Определяет тип сообщения на основе переданной строки с валютами.
 * - {@link #extractCurrencyCode(String)}: Извлекает код валюты из переданного сообщения.
 * - {@link #isValidCurrencyCode(String)}: Проверяет корректность кода валюты или списка валют.
 * - {@link #serializeToJson(ResponseToKafkaServer)}: Сериализует объект ResponseToKafkaServer в JSON-строку.
 * - {@link #processCurrencyData(String, String)}: Обрабатывает данные валюты и проверяет коды валют на валидность.
 * - {@link #saveResponseInDb(String, String)}: Получает данные от стороннего API и сохраняет их в базе данных.
 * - {@link #getDataFromDB(String, String)}: Извлекает данные из базы данных и отправляет их в топик Kafka.
 * - {@link #updateResponseInDb(String, String)}: Обновляет данные в базе данных новыми данными от стороннего API.
 * - {@link #serializeResponseAndSendToTopic(ResponseToKafkaServer, String)}: Сериализует объект ответа и отправляет его в топик.
 * - {@link #sendToKafka(String, String)}: Отправляет сообщение в указанный топик Kafka.
 * - {@link #checkLastSavePayload(Date, String, String, String)}: Проверяет актуальность данных по курсам валют и обновляет их при необходимости.
 * - {@link #doesResponseExist(String)}: Проверяет наличие записи с указанной валютой в базе данных.
 * - {@link #findDataByCurrency(String)}: Извлекает данные из базы данных по коду валюты.
 * - {@link #saveResponse(ResponseToKafkaServer)}: Сохраняет данные о валютах и курсах в базу данных PostgreSQL.
 *
 * Исключения:
 * - {@link NullOrEmptyArgumentException}: выбрасывается, если аргументы метода равны null или пустым строкам.
 * - {@link NotFoundObjectInDbException}: выбрасывается, если объект не найден в базе данных.
 * - {@link FailedResponseException}: выбрасывается при ошибках, связанных с ответами от стороннего API.
 * - {@link FailSaveException}: выбрасывается при ошибке сохранения данных в базе данных.
 * - {@link FailUpdateException}: выбрасывается при ошибке обновления данных в базе данных.
 * - {@link FailConnectException}: выбрасывается при ошибке подключения к базе данных или API.
 * - {@link RuntimeException}: стандартное системное исключение выбрасывается, если возникает ошибка при выполнении операции.
 *
 *
 * Аннотации:
 * - @Service: указывает, что класс является сервисом и управляется Spring-контейнером.
 * - @Transactional: обеспечивает атомарность выполнения операций, предотвращая их частичное выполнение.
 */
@Service
public class KafkaListenerService
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CurrencyKafkaListener.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private CurrencyService currencyService;
    private RequestIdService requestIdService;
    private SavePayloadMessageService savePayloadMessageService;
    private ResponseToKafkaServerepository responseToKafkaServerepository;
    //endRegion

    //region Constructor
    public KafkaListenerService(KafkaTemplate<String, String> kafkaTemplate, CurrencyService currencyService,
                                RequestIdService requestIdService, SavePayloadMessageService savePayloadMessageService,
                                 ResponseToKafkaServerepository responseToKafkaServerepository)
    {
        this.kafkaTemplate = kafkaTemplate;
        this.currencyService = currencyService;
        this.requestIdService = requestIdService;
        this.savePayloadMessageService = savePayloadMessageService;
        this.responseToKafkaServerepository = responseToKafkaServerepository;
    }
    //endRegion

    //region Methods
    /**
     * Метод saveRequestId:
     * Сохраняет requestId и expirationDate в базе данных MongoDB, в коллекции "requestId-collection",
     * для статистики и понимания каких запросов было больше всего,
     * в течении установленной даты expirationDate, по истечении которой он будет удалён
     * (работа с базой данных MongoDB)
     *
     * @param requestId - уникальный идентификатор запроса requestId
     *
     * @exception FailSaveException - при проблеме с сохранением объекта в базе данных
     * @exception FailConnectException - при проблеме с соединением с базой данных
     * **/
    @Transactional
    public void saveRequestId(String requestId)
    {
        if (requestId == null || requestId.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", requestId);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            // нужно сохранить requestId в базе данных MongoDB, в коллекции requestId-collection:
            // и установить ему срок expirationDate, по истечении которого, его нужно будет удалить
            Date expirationDate = requestIdService.createExpirationDate(); // создаём дату, которая будет являться датой истечения срока действия requestId
            requestIdService.saveRequestId(requestId, expirationDate); // сохраняем в базе данных requestId (для последующих проверок запросов на дубли)
        }
        catch (FailSaveException ex)
        {
            logger.error("Невозможно сохранить объект в базе данных");
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("Невозможно соединение с базой данных");
            throw ex;
        }
    }

    /**
     * Метод savePayloadInDb:
     *
     * Описание метода:
     * Сохранения payload (полезной нагрузки сообщения currencies) в базе данных PostgreSQL в таблице payload_table
     * currencies в столбец pay_load
     * currentDateTime в столбец last_save_payload (текущее время сохранения на момент операции)
     *
     * @param currencies - полезная нагрузка (например: "ALL:" или "SINGLE:{USD}", или "FILTER:{USD, EUR, RUB}")
     *
     * @exception FailSaveException - при проблеме с сохранением объекта в базе данных
     * @exception FailConnectException - при проблеме с соединением с базой данных
     * **/
    @Transactional
    public void savePayloadInDb(String currencies)
    {
        if (currencies == null || currencies.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", currencies);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            System.out.println("Проверка был ли уже такой запрос ранее, т.е. он сохранён в базе данных? С currencies: " + currencies);
            // сохраняем сам запрос, т.е. саму полезную нагрузку payLoad из пришедшего сообщения:
            PayloadOfMessage payloadOfMessage = new PayloadOfMessage();
            payloadOfMessage.setPayLoad(currencies);

            // Установить текущую дату и время
            LocalDateTime currentDateTime = LocalDateTime.now(); // Получаем текущее локальное время
            payloadOfMessage.setLastSavePayload(Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant())); // Преобразуем в Date

            savePayloadMessageService.savePayloadMessage(payloadOfMessage); // сохраняем в базе данных PostgreSQL в таблице "payLoad_table"
            System.out.println("ПОЛЕЗНАЯ НАГРУЗКА УСПЕШНО СОХРАНЕНА В базе данных PostgreSQL в таблице payLoad_table ");
        }
        catch (FailSaveException ex)
        {
            logger.error("Невозможно сохранить объект в базе данных");
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("Невозможно соединение с базой данных");
            throw ex;
        }
    }

    /**
     * Метод updatePayloadInDb:
     *
     * Описание метода:
     * Обновление поля "last_save_payload" (дата последнего обновления запроса) в базе данных PostgreSQL в таблице payload_table
     * currencies в столбец pay_load
     * currentDateTime в столбец last_save_payload (текущее время сохранения на момент операции)
     *
     * @param currencies - полезная нагрузка (например: "ALL:" или "SINGLE:{USD}", или "FILTER:{USD, EUR, RUB}")
     *
     * @exception FailSaveException - при проблеме с сохранением объекта в базе данных
     * @exception FailUpdateException - при проблеме с сохранением объекта в базе данных
     * @exception FailConnectException - при проблеме с соединением с базой данных
     * **/
    @Transactional
    public void updatePayloadInDb(String currencies)
    {
        if (currencies == null || currencies.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", currencies);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            // Получаем текущую дату для обновления записи
            Date currentDate = new Date();

            // Обновляем данные о последнем сохранении в базе данных
            savePayloadMessageService.updateLastSavePayload(currencies, currentDate);
        }
        catch (FailSaveException ex)
        {
            logger.error("Невозможно сохранить объект в базе данных");
            throw ex;
        }
        catch (FailUpdateException ex)
        {
            logger.error("Невозможно обновить объект в базе данных");
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("Невозможно соединение с базой данных");
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


    /**
     * Метод extractMessageType:
     * Определяет тип сообщения на основе переданной строки с валютами.
     * Метод проверяет, начинается ли строка с определенных префиксов
     * и возвращает соответствующий тип сообщения.
     * Если строка не соответствует ожидаемым форматам, выбрасывается исключение.
     *
     * @param currencies входная строка, содержащая информацию о валютах.
     * @return тип сообщения, извлеченный из строки (может быть "ALL", "SINGLE" или "FILTER").
     * @throws RuntimeException если тип сообщения не распознан.
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * @exception MassageNotRecognizedException - при проблеме с распознаванием сообщения
     */
    public String extractMessageType(String currencies)
    {
        if (currencies == null || currencies.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", currencies);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        // Проверяем, начинается ли строка с "ALL:"
        if (currencies.startsWith("ALL:"))
        {
            return "ALL"; // Возвращаем тип "ALL"
        }
        // Проверяем, начинается ли строка с "SINGLE:"
        else if (currencies.startsWith("SINGLE:"))
        {
            return "SINGLE"; // Возвращаем тип "SINGLE"
        }
        // Проверяем, начинается ли строка с "FILTER:"
        else if (currencies.startsWith("FILTER:"))
        {
            return "FILTER"; // Возвращаем тип "FILTER"
        }
        else
        {
            logger.error("Неизвестный тип сообщения{}: ", currencies);
            // Выбрасываем исключение, если тип сообщения не распознан
            throw new MassageNotRecognizedException("Неизвестный тип сообщения: " + currencies);
        }
    }

    /**
     * Метод extractCurrencyCode:
     * Извлекает код валюты из переданного сообщения.
     * Метод проверяет, начинается ли сообщение с определенных префиксов
     * и возвращает соответствующий код валюты или выбрасывает исключение,
     * если сообщение не соответствует ожидаемому формату.
     *
     * @param message входящее сообщение, из которого необходимо извлечь код валюты.
     * @return код валюты, извлеченный из сообщения.
     * @throws MassageNotRecognizedException если сообщение не содержит кода валюты или не распознано.
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     */
    @Transactional
    public String extractCurrencyCode(String message)
    {
        if (message == null || message.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", message);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        // Проверяем, начинается ли сообщение с "FILTER:" или "SINGLE:"
        if (message.startsWith("FILTER:") || message.startsWith("SINGLE:"))
        {
            String[] parts = message.split(":");

            // Проверяем, что в массиве есть как минимум два элемента
            if (parts.length > 1)
            {
                return parts[1].trim(); // Убираем лишние пробелы и возвращаем код валюты
            }
            else
            {
                logger.error("Сообщение {} не содержит кода валюты. ", message);
                // Выбрасываем исключение, если код валюты отсутствует
                throw new MassageNotRecognizedException("Сообщение не содержит кода валюты: " + message);
            }
        }
        else if (message.startsWith("ALL:"))
        {
            // Если сообщение начинается с "ALL:", возвращаем специальный код
            return "ALL"; // Возвращаем ALL
        }
        else
        {
            logger.error("Сообщение{} не распознано: ", message);
            // Выбрасываем исключение, если сообщение не распознано
            throw new MassageNotRecognizedException("Сообщение не распознано: " + message);
        }
    }


    /**
     * Метод isValidCurrencyCode:
     * Предназначен для проверки корректности кода валюты или списка валют.
     *
     * @param currencyCodes - строка с кодом валюты или списком валют через запятую.
     * @return true, если все коды валют допустимы; иначе false.
     */
    @Transactional
    public boolean isValidCurrencyCode(String currencyCodes)
    {
        if (currencyCodes == null || currencyCodes.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", currencyCodes);
            return false;
        }

        // Разделить строку на отдельные валюты, удалить лишние пробелы и проверить каждую
        String[] codes = currencyCodes.split(",");
        for (String code : codes)
        {
            if (!code.trim().matches("[A-Z]{3}"))
            {
                return false; // Вернуть false, если хотя бы один код не соответствует формату
            }
        }
        return true; // Все коды валют корректны
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
     * Метод processCurrencyData:
     * Предназначен для обработки данных валюты и
     * проверки кодов валют на валидность
     *
     * @param currencyCode - код валюты
     * @param requestId - уникальный идентификатор запроса requestId
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * @exception FailSaveException - при проблеме с сохранением объекта в базе данных
     * @exception FaileMappinhToJSON - при проблеме с трансформацией объекта в Json
     * @exception FailConnectException - при проблеме с соединением с базой данных
     */
    @Transactional
    public void processCurrencyData(String currencyCode, String requestId)
    {
        if (currencyCode == null || currencyCode.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", currencyCode);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        if (requestId == null || requestId.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", requestId);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            System.out.println("Обрабатываем данные для валюты: " + currencyCode);

            // Проверяем код валюты на валидность (большие латинские буквы, не более трёх символов):
            if (!isValidCurrencyCode(currencyCode))
            {
                logger.error("Некорректный код валюты {}:", currencyCode);
                System.out.println("Некорректный код валюты:" + currencyCode);
                throw new RuntimeException("Некорректный код валюты: " + currencyCode);
            }

            // сохранение в базе данных:
            ResponseToKafkaServer responseToKafkaServer = saveResponseInDb(currencyCode, requestId); // сохраняем в базе данных ответ от стороннего API и возвращаем объект responseToKafkaServer для ответа в топик

            //сериализация сообщения и отправка его в топик:
            serializeResponseAndSendToTopic(responseToKafkaServer, requestId); // Сериализация объекта ResponseToKafkaServer в строку и отправка ответа в топик с идентификатором запроса равным requestId
        }
        catch (FailSaveException ex)
        {
            logger.error("Невозможность сохранения данных в базе данных ");
            throw  ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("Невозможность соединения с базой данных ");
            throw  ex;
        }
        catch (FaileMappinhToJSON ex)
        {
            logger.error("Невозможность трансформации объекта в Json ");
            throw ex;
        }
    }


    /**
     * Метод saveResponseInDb:
     * Получает данные от стороннего API, устанавливает идентификатор запроса,
     * сохраняет полученный ответ в базе данных и возвращает объект ответа.
     *
     * @param currencyCode код валюты, для которой необходимо получить обменный курс.
     * @param requestId уникальный идентификатор запроса, который будет установлен в ответе.
     * @return объект ResponseToKafkaServer, содержащий данные об обменном курсе.
     *
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * @exception FailSaveException - при проблеме с сохранением объекта в базе данных
     * @exception NotFoundObjectInDbException - при проблеме с получением данных от стороннего API
     * @exception FailConnectException - при проблеме с соединением с базой данных
     */
    @Transactional
    public ResponseToKafkaServer saveResponseInDb(String currencyCode, String requestId)
    {
        if (currencyCode == null || currencyCode.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", currencyCode);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        if (requestId == null || requestId.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", requestId);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        try
        {
            // Логируем начало получения данных от стороннего API
            System.out.println("Получаем данные от стороннего API............");

            // Получаем ответ от стороннего API
            ResponseToKafkaServer responseToKafkaServer = currencyService.getExchangeRate(currencyCode);

            // Устанавливаем requestId в ответ от стороннего API
            responseToKafkaServer.setRequestId(requestId);

            // Логируем полученные данные от стороннего API
            System.out.println("Получены данные от стороннего API: " + responseToKafkaServer.getRates());

            // Сохраняем ответ от стороннего API в базе данных
            saveResponse(responseToKafkaServer);

            // Возвращаем объект ответа
            return responseToKafkaServer;
        }
        catch (FailSaveException ex)
        {
            logger.error("Невозможность сохранения данных в базе данных ");
            throw ex;
        }
        catch (NotFoundObjectInDbException ex)
        {
            logger.error("Невозможность нахождения данных в базе данных ");
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("Невозможность соединения с базой данных ");
            throw  ex;
        }
    }


    /**
     * Метод getDataFromDB:
     * Извлекает данные из базы данных на основе переданных параметров.
     * Проверяет, существуют ли данные в базе, и если да,
     * получает объект ответа и отправляет его в указанный топик Kafka в формате JSON.
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
    public void getDataFromDB(String extractCurrencyFromMessage, String requestId)
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
            if (doesResponseExist(extractCurrencyFromMessage)) // проверяем, если ответ true, т.е. такие данные есть в базе данных, то:
            {
                // Получаем заполненный объект ResponseToKafkaServer из базы данных "response_to_kafka"

                ResponseToKafkaServer responseToKafkaServer = responseToKafkaServerepository.findByCurrencyWithRates(extractCurrencyFromMessage); // получаем коды валют и их курсы из базы данных

                String responseMessageToTopic = serializeToJson(responseToKafkaServer); // Сериализуем ответ в JSON

                sendToKafka(requestId, responseMessageToTopic); // Отправляем данные в топик

                // Логируем информацию об отправке данных
                System.out.println("данные из базы данных отправлены в топик!"); // Логируем информацию об отправке данных
                logger.info("Ответ: {} взят из базы данных и отправлен в топик!", responseToKafkaServer); // логируем информацию об успешной отправки ответа в топик
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
     * Метод updateResponseInDb:
     * Обновляем данные в базе данных, данными полученными от стороннего API
     *
     * @param currencyCode - тип сообщения и список с кодами валют (для примера: "ALL" или "USD,EUR,RUB" или "USD")
     * @param requestId - уникальный идентификатор запроса
     *
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * @exception FailSaveException - при проблеме с сохранением объекта в базе данных
     * @exception FailUpdateException - при проблеме с обновлением объекта в базе данных
     * @exception FailResponseFromApiException - при проблеме с получением данных от стороннего API
     * @exception FailConnectException - при проблеме с соединением с базой данных
     * @exception NullOrEmptyArgumentException - при проблеме с передачей пустого аргумента
     * **/
    @Transactional
    public ResponseToKafkaServer updateResponseInDb(String currencyCode, String requestId)
    {
        if (currencyCode == null || currencyCode.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент{}", currencyCode);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        if (requestId == null || requestId.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент{}", requestId);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            System.out.println("Метод обновления данных!!! Хранение данных больше часа!!!!! Меняем на данные из стороннего API !!!");
            System.out.println("Получаем данные от стороннего API............");
            //получаем новый ответ от стороннего API на ответ на аналогичный запрос:
            ResponseToKafkaServer responseToKafkaServer = currencyService.getExchangeRate(currencyCode); // получаем ответ от стороннего API

            //получаем из базы данных старый ответ на аналогичный запрос:
            ResponseToKafkaServer oldSavedResponse = findDataByCurrency(currencyCode); // получаем старое значение, т.е. заполненный объект ResponseToKafkaServer из базы данных "response_to_kafka"
            System.out.println("Печатаем старые данные SavedResponse: " + oldSavedResponse);

            // обновляем старый ответ новыми значениями
            oldSavedResponse.setCurrency(responseToKafkaServer.getCurrency()); // обновляем поле Currency
            oldSavedResponse.setBaseCurrency(responseToKafkaServer.getBaseCurrency()); // обновляем поле
            oldSavedResponse.setRates(responseToKafkaServer.getRates()); // обновляем поле BaseCurrency
            oldSavedResponse.setDate(responseToKafkaServer.getDate()); // обновляем поле Date
            oldSavedResponse.setRequestId(requestId); // обновляем поле RequestId
            System.out.println("Получены данные от стороннего API: " + responseToKafkaServer.getRates());

            // сохраняем данные в БД:
            saveResponse(oldSavedResponse); // сохраняем ответ от стороннего API в базе данных
            System.out.println("Печатаем обновлённые данные oldSavedResponse: " + oldSavedResponse);

            return oldSavedResponse; // возвращаем обновлённый ответ
        }
        catch (FailResponseFromApiException ex)
        {
            logger.error("Проблемы получения данных от стороннего API");
            throw ex;
        }
        catch (FailSaveException ex)
        {
            logger.error("Невозможность сохранения данных в базе данных");
            throw ex;
        }
        catch (FailUpdateException ex)
        {
            logger.error("Невозможность обновления данных в базе данных");
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("Невозможно соединение с базой данных");
            throw ex;
        }
    }



    /**
     * Метод serializeResponseAndSendToTopic:
     * Сериализует объект ответа в формат JSON и отправляет его в указанный топик Kafka.
     * Метод принимает объект ответа и уникальный идентификатор запроса,
     * затем преобразует ответ в строку JSON и вызывает метод отправки сообщения в Kafka.
     *
     * @param responseToKafkaServer объект ответа, который будет сериализован и отправлен в топик.
     * @param requestId уникальный идентификатор запроса, используемый в качестве ключа для сообщения.
     *
     * @exception FaileMappinhToJSON - при проблеме с трансформацией объекта в Json
     * @exception NullOrEmptyArgumentException - при проблеме с передачей пустого аргумента
     * @exception KafkaException - при проблеме с передачей сообщения в топик кафка
     *
     */
    public void serializeResponseAndSendToTopic(ResponseToKafkaServer responseToKafkaServer, String requestId)
    {
        if (responseToKafkaServer == null)
        {
            logger.error("Передан пустой или равный nul аргумент {}", responseToKafkaServer);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        if (requestId == null || requestId.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент {}", requestId);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            // Сериализуем ответ в JSON
            String responseMessageToTopic = serializeToJson(responseToKafkaServer);

            // Логируем окончательный ответ по фильтру валют перед отправкой в топик
            System.out.println("Окончательный ответ по фильтру валют перед отправкой в топик response-currency-topic: " + responseToKafkaServer.getRates());

            // Метод отправки сообщения в топик
            sendToKafka(requestId, responseMessageToTopic);
        }
        catch (FaileMappinhToJSON ex)
        {
            logger.error("Проблемы c трансформацией объекта в Json");
            throw ex;
        }
        catch (KafkaException ex)
        {
            logger.error("Проблемы с передачей сообщения в топик кафка");
            throw ex;
        }
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
    public void sendToKafka(String requestId, String responseMessageToTopic)
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
                    "response-topic", // Название топика
                    requestId, // Ключ (можно указать null, если ключ не нужен)
                    responseMessageToTopic // Полезная нагрузка
            );

            // Добавляем заголовки, если необходимо
            producerRecord.headers().add("kafka_messageKey", requestId.getBytes(StandardCharsets.UTF_8));
            producerRecord.headers().add("kafka_correlationId", requestId.getBytes(StandardCharsets.UTF_8));

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
     * Метод checkLastSavePayload:
     * Предназначен для проверки актуальности данных по курсам валют сохранённых в базе данных
     * Если данным больше либо равны одному часу, то: берём данные от стороннего API и обновляем данные в базе данных
     * Если данным меньше одного часа, то: берём данные из базы данных и отсылаем их в топик
     *
     * @param lastSavePayload - дата последнего сохранения полезной нагрузки currency в базе данных PostgreSQL таблице "pay_load_table"
     *
     * @exception NullOrEmptyArgumentException - при проблеме с передачей в качестве аргумента null
     * @exception FailSaveException - при проблеме с сохранением объекта в базе данных
     * @exception FailUpdateException - при проблеме с обновлением объекта в базе данных
     * @exception MassageNotRecognizedException - при проблеме трансформации объекта в Json
     * @exception FailConnectException - при проблеме с соединением с базой данных
     * @exception NotFoundObjectInDbException - при проблеме нахождения объекта в базе данных
     * **/
    @Transactional
    public void checkLastSavePayload(Date lastSavePayload, String payload, String requestId, String extractCurrencyFromMessage)
    {
        if (lastSavePayload == null)
        {
            logger.error("Передан пустой или равный nul аргумент{}", lastSavePayload);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
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

            getDataFromDB(extractCurrencyFromMessage, requestId);  // берём данные из базы данных


            // Проверяем, больше ли разница 1 часа
            if (duration.toHours() >= 1)
            {
                // Время больше или равно 1 часу
                System.out.println("Время больше или равно 1 часа. Данные устарели! Выполняем действия: запрос актуальных данных от стороннего API и отправка их в топик");

                // обновляем дату актаульности хранения данных "last_save_payload в в таблице "payload_table"
                updatePayloadInDb(payload); //обновляем дату актуальности сохранения запроса

                ResponseToKafkaServer updatedResponse = updateResponseInDb(extractCurrencyFromMessage, requestId); // обновляет объект ResponseToKafkaServer в таблице "response_to_kafka"

                serializeResponseAndSendToTopic(updatedResponse, requestId); // сериализуем объект  ResponseToKafkaServer String и отправляем в топик
            }
        }
        catch (NotFoundObjectInDbException ex)
        {
            logger.error("невозможно сохранить объект в базе данных");
            throw ex;
        }
        catch (FailUpdateException ex)
        {
            logger.error("невозможно обновить объект в базе данных");
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("невозможно установить связь с базой данных");
            throw ex;
        }
        catch (FaileMappinhToJSON ex)
        {
            logger.error("невозможно трансформировать объект в Json");
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
     * Метод findDataByCurrency:
     * получаем данные (заполненный объект ResponseToKafkaServer) из базы данных
     *
     * @param messageCurrency - сообщение с кодами валют
     *
     * @exception NotFoundObjectInDbException - при невозможности найти объект в базе данных
     * @exception FailConnectException - при невозможности соединения с базой данных
     * @exception NullOrEmptyArgumentException - при передаче нулевого аргумента
     * **/
    public ResponseToKafkaServer findDataByCurrency(String messageCurrency)
    {
        if (messageCurrency == null || messageCurrency.isEmpty())
        {
            logger.error("Передан пустой или равный nul аргумент{}!", messageCurrency);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }

        try
        {
            return responseToKafkaServerepository.findByCurrencyWithRates(messageCurrency);
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
     * Метод saveResponse:
     * сохранение данных о валютах и курсах в базу данных PostgreSQL таблицу "response_to_kafka"
     *
     * @param responseToKafkaServer - объект типа ResponseToKafkaServer (ответ для кафка)
     *
     * @exception NullOrEmptyArgumentException - при передаче нулевого аргумента
     * **/
    public void saveResponse(ResponseToKafkaServer responseToKafkaServer)
    {
        if (responseToKafkaServer == null)
        {
            logger.error("Передан пустой или равный nul аргумент {}", responseToKafkaServer);
            throw new NullOrEmptyArgumentException("Передан пустой или равный nul аргумент!");
        }
        try
        {
            responseToKafkaServerepository.save(responseToKafkaServer); // сохранение ответа в базе данных
        }
        catch (FailSaveException ex)
        {
            logger.error("невозможно сохранить объект {} в базе данных", responseToKafkaServer);
            throw ex;
        }
        catch (FailConnectException ex)
        {
            logger.error("невозможно установить связь с базой данных");
            throw ex;
        }
    }

    //endRegion
}

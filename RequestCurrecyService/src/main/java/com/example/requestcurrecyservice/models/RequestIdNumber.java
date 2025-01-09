package com.example.requestcurrecyservice.models;

import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * Класс RequestIdNumber:
 *
 * Представляет сущность, которая соответствует документу в коллекции "requestId-collection" в базе данных MongoDB.
 * Содержит уникальный идентификатор запроса, дату истечения срока действия и идентификатор документа.
 *
 * Поля:
 * - id: уникальный номер, под которым хранятся записи в коллекции "requestId-collection".
 * - requestId: уникальный номер сообщения из Kafka-топика.
 * - expirationDate: дата истечения срока действия номера запроса.
 *
 * Конструкторы:
 * - {@link #RequestIdNumber(String, Date)}: Конструктор с параметрами для инициализации полей requestId и expirationDate.
 * - {@link #RequestIdNumber()}: Конструктор по умолчанию.
 *
 * Методы:
 *
 * - {@link #getExpirationDate()}: Возвращает значение поля expirationDate.
 * - {@link #setExpirationDate(Date)}: Устанавливает значение поля expirationDate.
 *
 * Аннотации:
 * - @Component: Указывает, что класс является компонентом Spring и может быть управляем контейнером.
 */
@Component
public class RequestIdNumber
{
    //region Fields
    private String requestId; // уникальный номер сообщения из топика кафка
    private Date expirationDate; // дата истечения срока действия номера
    //endRegion

    //region Constructors
    public RequestIdNumber(String requestId, Date expirationDate)
    {
        this.requestId = requestId;
        this.expirationDate = expirationDate;
    }
    public RequestIdNumber()
    {
        // default constructor
    }
    //endregion

    //region Getters
    public Date getExpirationDate()
    {
        return expirationDate;
    }
    //endRegion

    //region Setters
    public void setExpirationDate(Date expirationDate)
    {
        this.expirationDate = expirationDate;
    }
    //endRegion
}

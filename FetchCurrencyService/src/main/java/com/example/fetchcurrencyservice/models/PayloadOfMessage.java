package com.example.fetchcurrencyservice.models;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Класс PayloadOfMessage:
 *
 * Представляет сущность, которая соответствует таблице "payLoad_table" в базе данных.
 * Содержит полезное сообщение и дату последнего сохранения. Используется для хранения
 * информации о сообщениях, отправляемых в приложении.
 *
 * Поля:
 * - id: уникальный идентификатор записи в таблице "payLoad_table".
 * - payLoad: полезное сообщение, которое может содержать информацию о конкретной валюте или список валют.
 * - lastSavePayload: дата и время последнего сохранения полезного сообщения.
 *
 * Конструкторы:
 * - {@link #PayloadOfMessage(String, Date)}: Конструктор с параметрами для инициализации полей payLoad и lastSavePayload.
 * - {@link #PayloadOfMessage()}: Конструктор по умолчанию.
 *
 * Методы:
 * - {@link #getId()}: Возвращает значение поля id.
 * - {@link #getPayLoad()}: Возвращает значение поля payLoad.
 * - {@link #getLastSavePayload()}: Возвращает значение поля lastSavePayload.
 * - {@link #setPayLoad(String)}: Устанавливает значение поля payLoad.
 * - {@link #setLastSavePayload(Date)}: Устанавливает значение поля lastSavePayload.
 *
 * Аннотации:
 * - @Entity: Указывает, что класс является сущностью и будет отображен в таблице базы данных.
 * - @Table: Определяет имя таблицы в базе данных, с которой связана сущность.
 * - @Id: Указывает, что поле id является уникальным идентификатором.
 * - @GeneratedValue: Указывает, что значение поля id будет автоматически сгенерировано.
 * - @Column: Указывает свойства столбцов в таблице (имя столбца и ограничение на null).
 */
@Entity
@Table(name = "payLoad_table")
public class PayloadOfMessage
{
    //region Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // идентификационный номер в таблице "payLoad_table"

    @Column(name = "payLoad", nullable = false)
    private String payLoad; // полезное сообщение (ALL: SINGLE:{конкретная валюта} FILTER:{список валют})

    @Column(name = "last_save_payload", nullable = false)
    private Date lastSavePayload;
    //endRegion

    //region Constructors
    public PayloadOfMessage(String payLoad,  Date lastSavePayload)
    {
        this.payLoad = payLoad;

        this.lastSavePayload = lastSavePayload;
    }
    public PayloadOfMessage()
    {
        // default constructor
    }
    //endRegion

    //region Getters
    public Long getId()
    {
        return id;
    }
    public String getPayLoad()
    {
        return payLoad;
    }

    public Date getLastSavePayload()
    {
        return lastSavePayload;
    }
    //endRegion

    //region Setters
    public void setPayLoad(String payLoad)
    {
        this.payLoad = payLoad;
    }
    public void setLastSavePayload(Date lastSavePayload)
    {
        this.lastSavePayload = lastSavePayload;
    }
    //endRegion
}

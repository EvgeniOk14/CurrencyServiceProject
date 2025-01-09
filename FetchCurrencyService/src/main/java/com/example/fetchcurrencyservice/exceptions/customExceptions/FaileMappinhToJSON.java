package com.example.fetchcurrencyservice.exceptions.customExceptions;

/**
 * Класс FaileMappinhToJSON.
 *
 * Исключение, которое выбрасывается в случае неудачного преобразования (маппинга) данных в формат JSON.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с неудачными операциями сериализации объектов в JSON, например,
 * при использовании библиотек для работы с JSON.
 *
 * Конструкторы:
 * - FaileMappinhToJSON(String message): Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - getMessage(): Возвращает сообщение об ошибке, связанное с исключением.
 */
public class FaileMappinhToJSON extends RuntimeException
{
    public FaileMappinhToJSON(String message)
    {
        super(message);
    }
}

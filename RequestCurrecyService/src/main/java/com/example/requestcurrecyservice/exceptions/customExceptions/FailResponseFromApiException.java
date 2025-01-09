package com.example.requestcurrecyservice.exceptions.customExceptions;

/**
 * Класс FailResponseFromApiException.
 *
 * Исключение, которое выбрасывается в случае получения неудачного ответа от API.
 * Это исключение является наследником стандартного класса {@link RuntimeException} и может использоваться для
 * обозначения ошибок, связанных с неудачными запросами к внешним API, когда ответ не соответствует
 * ожидаемым результатам или содержит ошибку.
 *
 * Конструкторы:
 *
 * - {@link #FailResponseFromApiException(String)}: Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - {@link #getMessage()}: Возвращает сообщение об ошибке, связанное с исключением.
 *
 */

public class FailResponseFromApiException extends RuntimeException
{
    public FailResponseFromApiException(String message)
    {
        super(message);
    }
}


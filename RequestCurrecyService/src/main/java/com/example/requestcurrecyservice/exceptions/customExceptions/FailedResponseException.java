package com.example.requestcurrecyservice.exceptions.customExceptions;

/**
 * Класс FailedResponseException.
 *
 * Исключение, которое выбрасывается в случае получения неудачного ответа от API.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с неудачными запросами к внешним API, когда ответ не соответствует
 * ожидаемым результатам или содержит ошибку.
 *
 * Конструкторы:
 * - FailedResponseException(String message): Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - getMessage(): Возвращает сообщение об ошибке, связанное с исключением.
 */
public class FailedResponseException extends RuntimeException
{
    public FailedResponseException(String message)
    {
        super(message);
    }

}

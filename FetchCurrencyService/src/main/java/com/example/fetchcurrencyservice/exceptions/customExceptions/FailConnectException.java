package com.example.fetchcurrencyservice.exceptions.customExceptions;

/**
 * Класс FailConnectException.
 *
 * Исключение, которое выбрасывается в случае неудачного подключения к ресурсу или сервису.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с попытками установить соединение, например, при подключении к
 * базе данных, веб-сервису или другому удаленному ресурсу.
 *
 * Конструкторы:
 * - FailConnectException(String message): Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - getMessage(): Возвращает сообщение об ошибке, связанное с исключением.
 */
public class FailConnectException extends RuntimeException
{
    public FailConnectException(String message)
    {
        super(message);
    }
}

package com.example.requestcurrecyservice.exceptions.customExceptions;

/**
 * Класс IncorrectCurrencyCode:
 *
 * Исключение, которое выбрасывается в случае, если код валюты некорректен.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с неверными или неподдерживаемыми кодами валют в приложении.
 *
 * Конструкторы:
 * - {@link #IncorrectCurrencyCode(String)}: Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - {@link #getMessage()}: Возвращает сообщение об ошибке, связанное с исключением.
 */
public class IncorrectCurrencyCode extends RuntimeException
{
    public IncorrectCurrencyCode(String message)
    {
        super(message);
    }
}

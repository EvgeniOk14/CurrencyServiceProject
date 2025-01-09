package com.example.fetchcurrencyservice.exceptions.customExceptions;

/**
 * Класс MassageNotRecognizedException:
 *
 * Исключение, которое выбрасывается в случае, если сообщение не распознано.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с обработкой сообщений в приложении.
 *
 * Конструкторы:
 * - {@link #MassageNotRecognizedException(String)}: Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - {@link #getMessage()}: Возвращает сообщение об ошибке, связанное с исключением.
 */
public class MassageNotRecognizedException extends RuntimeException
{
    public MassageNotRecognizedException(String message)
    {
        super(message);
    }
}

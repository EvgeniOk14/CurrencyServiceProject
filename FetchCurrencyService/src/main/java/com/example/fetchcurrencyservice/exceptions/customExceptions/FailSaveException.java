package com.example.fetchcurrencyservice.exceptions.customExceptions;

/**
 * Класс FailSaveException:
 *
 * Исключение, которое выбрасывается в случае неудачного сохранения данных.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с неудачными операциями сохранения в приложении, например, при
 * попытке сохранить запись в базе данных, которое не было выполнено успешно.
 *
 * Конструкторы:
 * - {@link #FailSaveException(String)}: Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - {@link #getMessage()}: Возвращает сообщение об ошибке, связанное с исключением.
 */
public class FailSaveException extends RuntimeException
{
    public FailSaveException(String message)
    {
        super(message);
    }
}


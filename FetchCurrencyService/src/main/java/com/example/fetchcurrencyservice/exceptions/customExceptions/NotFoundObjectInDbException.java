package com.example.fetchcurrencyservice.exceptions.customExceptions;

/**
 * Класс NotFoundObjectInDbException:
 *
 * Исключение, которое выбрасывается в случае, если объект не найден в базе данных.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с отсутствием запрашиваемых данных в базе данных.
 *
 * Конструкторы:
 * - {@link #NotFoundObjectInDbException(String)}: Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - {@link #getMessage()}: Возвращает сообщение об ошибке, связанное с исключением.
 */
public class NotFoundObjectInDbException  extends RuntimeException
{
    public NotFoundObjectInDbException(String message)
    {
        super(message);
    }
}

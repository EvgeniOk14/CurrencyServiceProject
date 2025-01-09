package com.example.requestcurrecyservice.exceptions.customExceptions;

/**
 * Класс NotSuccessCcreateObjectException:
 *
 * Исключение, которое выбрасывается в случае неудачного создания объекта.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с процессом создания объектов в приложении.
 *
 * Конструкторы:
 * - {@link #NotSuccessCcreateObjectException(String)}: Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - {@link #getMessage()}: Возвращает сообщение об ошибке, связанное с исключением.
 */
public class NotSuccessCcreateObjectException extends RuntimeException
{
    public NotSuccessCcreateObjectException(String message)
    {
        super(message);
    }
}

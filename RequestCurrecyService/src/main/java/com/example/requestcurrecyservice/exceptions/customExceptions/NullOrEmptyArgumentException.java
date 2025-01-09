package com.example.requestcurrecyservice.exceptions.customExceptions;

/**
 * Класс NullOrEmptyArgumentException:
 *
 * Исключение, которое выбрасывается в случае, если переданный аргумент равен null или является пустой строкой.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с некорректными входными данными в методах.
 *
 * Конструкторы:
 * - {@link #NullOrEmptyArgumentException(String)}: Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - {@link #getMessage()}: Возвращает сообщение об ошибке, связанное с исключением.
 */

public class NullOrEmptyArgumentException extends RuntimeException
{
    public NullOrEmptyArgumentException(String message)
    {
        super(message);
    }
}

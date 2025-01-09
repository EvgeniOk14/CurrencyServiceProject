package com.example.requestcurrecyservice.exceptions.customExceptions;

/**
 * Класс FailDeleteException.
 *
 * Исключение, которое выбрасывается в случае неудачного удаления объекта или записи.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с операциями удаления в приложении, например, при попытке удалить
 * несуществующий объект или при возникновении ошибки в процессе удаления.
 *
 * Конструкторы:
 * - FailDeleteException(String message): Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - getMessage(): Возвращает сообщение об ошибке, связанное с исключением.
 */
public class FailDeleteException extends RuntimeException
{
    public FailDeleteException(String message)
    {
        super(message);
    }
}



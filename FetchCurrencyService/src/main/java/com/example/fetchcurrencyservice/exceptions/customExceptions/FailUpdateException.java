package com.example.fetchcurrencyservice.exceptions.customExceptions;

/**
 * Класс FailUpdateException:
 *
 * Исключение, которое выбрасывается в случае неудачного обновления данных.
 * Это исключение является наследником стандартного класса RuntimeException и может использоваться для
 * обозначения ошибок, связанных с неудачными операциями обновления в приложении, например, при
 * попытке обновления записи в базе данных, которое не было выполнено успешно.
 *
 * Конструкторы:
 * - {@link #FailUpdateException(String)}: Создает новое исключение с заданным сообщением.
 *
 * Методы:
 * - {@link #getMessage()}: Возвращает сообщение об ошибке, связанное с исключением.
 */
public class  FailUpdateException extends RuntimeException
{
    public  FailUpdateException(String message)
    {
        super(message);
    }

}

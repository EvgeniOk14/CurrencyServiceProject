package com.example.servicecurrency.exceptions.customExceptions;

/**
 * Метод - NullOrEmptyArgumentException
 * Исключение, возникающее при передаче не корректного аргумента, путого или равного нулю
 */
public class NullOrEmptyArgumentException extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения NullOrEmptyArgumentException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public NullOrEmptyArgumentException(String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}

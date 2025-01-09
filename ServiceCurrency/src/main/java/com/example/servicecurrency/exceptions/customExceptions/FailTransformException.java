package com.example.servicecurrency.exceptions.customExceptions;

/**
 * Метод - FailTransformException
 * Исключение, возникающее при попытке трансформации (маппинга) одного объекта в другой
 */
public class FailTransformException  extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения FailTransformException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public FailTransformException(String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}


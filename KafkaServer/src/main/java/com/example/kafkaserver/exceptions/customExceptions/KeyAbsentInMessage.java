package com.example.kafkaserver.exceptions.customExceptions;

/**
 * Метод - KeyAbsentInMessage
 * Исключение, возникающее при отсутствие ключа в сообщении
 */
public class KeyAbsentInMessage   extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения KeyAbsentInMessage .
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public KeyAbsentInMessage (String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}
package com.example.kafkaserver.exceptions.customExceptions;

/**
 * Метод - MessageFailProccessingException
 * Исключение, возникающее при попытке чтения сообщения
 */
public class MessageFailProccessingException  extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения FailTransformException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public MessageFailProccessingException(String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}
package com.example.kafkaserver.exceptions.globalException;

import com.example.kafkaserver.exceptions.customExceptions.KeyAbsentInMessage;
import com.example.kafkaserver.exceptions.customExceptions.MessageFailProccessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Класс GlobalExceptionHandler:
 *
 * Обработчик глобальных исключений для приложения. Этот класс перехватывает и обрабатывает
 * исключения, возникающие в контроллерах, и возвращает соответствующие HTTP-ответы
 * с сообщениями об ошибках.
 *
 * Методы:
 * - {@link #handlerMessageFailProccessingException(MessageFailProccessingException)}: Обрабатывает исключение
 *   MessageFailProccessingException и возвращает статус BAD_REQUEST.
 * - {@link #handlerKeyAbsentInMessage(KeyAbsentInMessage)}: Обрабатывает исключение KeyAbsentInMessage
 *   и возвращает статус NOT_FOUND.
 */
@ControllerAdvice // Аннотация указывает, что класс является обработчиком исключений
public class GlobalExceptionHandler
{
    //region Fields
    private static final Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);
    //endRegion

    //region Methods
    /**
     * Обрабатывает исключение MessageFailProccessingException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом BAD_REQUEST
     */
    @ExceptionHandler(MessageFailProccessingException.class) // Аннотация указывает, что этот метод обрабатывает PasswordEmptyException
    public ResponseEntity<String> handlerMessageFailProccessingException(MessageFailProccessingException ex)
    {
        logger.info("Ошибка: Пустой пароль!: {}" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка - пустой пароль: " + ex.getMessage()); // Возвращает статус BAD_REQUEST с сообщением
    }

    /**
     * Обрабатывает исключение KeyAbsentInMessage
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом NOT_FOUND
     */
    @ExceptionHandler(KeyAbsentInMessage.class) // Аннотация указывает, что этот метод обрабатывает KeyAbsentInMessage
    public ResponseEntity<String> handlerKeyAbsentInMessage(KeyAbsentInMessage ex)
    {
        logger.info("Ошибка: Пустой пароль!: {}" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка - отсутсвие ключа в сообщении: " + ex.getMessage()); // Возвращает статус NOT_FOUND с сообщением
    }
    //endRegion
}

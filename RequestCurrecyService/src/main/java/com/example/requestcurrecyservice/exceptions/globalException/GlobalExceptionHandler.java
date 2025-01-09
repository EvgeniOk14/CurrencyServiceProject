package com.example.requestcurrecyservice.exceptions.globalException;

import com.example.requestcurrecyservice.exceptions.customExceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Класс GlobalExceptionHandler:
 *
 * Обработчик глобальных исключений для приложения. Использует аннотацию @ControllerAdvice для обработки
 * исключений, возникающих в контроллерах, и возвращает соответствующие ответы с сообщениями об ошибках
 * и статусами HTTP.
 *
 * Поля:
 * - logger: логгер для записи информации об исключениях.
 *
 * Методы:
 * - {@link #handlerNullOrEmptyArgumentException(NullOrEmptyArgumentException)}: Обрабатывает исключение
 *   NullOrEmptyArgumentException и возвращает сообщение об ошибке со статусом CONFLICT.
 * - {@link #handlerFailConnectException(FailConnectException)}: Обрабатывает исключение FailConnectException
 *   и возвращает сообщение об ошибке со статусом CONFLICT.
 * - {@link #handlerFailDeleteException(FailDeleteException)}: Обрабатывает исключение FailDeleteException
 *   и возвращает сообщение об ошибке со статусом CONFLICT.
 * - {@link #handlerFailSaveException(FailSaveException)}: Обрабатывает исключение FailSaveException
 *   и возвращает сообщение об ошибке со статусом CONFLICT.
 * - {@link #handlerNotFoundObjectInDbException(NotFoundObjectInDbException)}: Обрабатывает исключение
 *   NotFoundObjectInDbException и возвращает сообщение об ошибке со статусом CONFLICT.
 * - {@link #handlerFailUpdateException(FailUpdateException)}: Обрабатывает исключение FailUpdateException
 *   и возвращает сообщение об ошибке со статусом CONFLICT.
 * - {@link #handlerFailedResponseException(FailedResponseException)}: Обрабатывает исключение
 *   FailedResponseException и возвращает сообщение об ошибке со статусом NOT_FOUND.
 * - {@link #handlerMassageNotRecognizedException(MassageNotRecognizedException)}: Обрабатывает исключение
 *   MassageNotRecognizedException и возвращает сообщение об ошибке со статусом BAD_REQUEST.
 * - {@link #handlerIncorrectCurrencyCode(IncorrectCurrencyCode)}: Обрабатывает исключение IncorrectCurrencyCode
 *   и возвращает сообщение об ошибке со статусом BAD_REQUEST.
 * - {@link #handlerNotSuccessCcreateObjectException(NotSuccessCcreateObjectException)}: Обрабатывает исключение
 *   NotSuccessCcreateObjectException и возвращает сообщение об ошибке со статусом CONFLICT.
 * - {@link #handlerFaileMappinhToJSON(FaileMappinhToJSON)}: Обрабатывает исключение FaileMappinhToJSON
 *   и возвращает сообщение об ошибке со статусом CONFLICT.
 * - {@link #handlerFailResponseFromApiException(FailResponseFromApiException ex)}: Обрабатывает исключение FailResponseFromApiException
 *   и возвращает сообщение об ошибке со статусом CONFLICT.
 *
 * Аннотации:
 * - @ControllerAdvice: Указывает, что класс является обработчиком исключений для контроллеров Spring.
 */
@ControllerAdvice // Аннотация указывает, что класс является обработчиком исключений
public class GlobalExceptionHandler
{
    //region Fields
    private static final Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);
    //endRegion

    //region Methods
    /**
     * Обрабатывает исключение NullOrEmptyArgumentException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(NullOrEmptyArgumentException.class)
    public ResponseEntity<String> handlerNullOrEmptyArgumentException(NullOrEmptyArgumentException ex)
    {
        logger.info("Ошибка: невозможно создать объект " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: невозможно создать объект " + ex.getMessage());
    }
    /**
     * Обрабатывает исключение FailConnectException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(FailConnectException.class)
    public ResponseEntity<String> handlerFailConnectException(FailConnectException ex)
    {
        logger.info("Ошибка: нет соединения с базой данных! " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: нет соединения с базой данных! " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение FailDeleteException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(FailDeleteException.class)
    public ResponseEntity<String> handlerFailDeleteException(FailDeleteException ex)
    {
        logger.info("Ошибка: невозможно удалить объект из базы данных! " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: невозможно удалить объект из базы данных! " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение FailSaveException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(FailSaveException.class)
    public ResponseEntity<String> handlerFailSaveException(FailSaveException ex)
    {
        logger.info("Ошибка: невозможно сохранить объект в базе данных! " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: невозможно сохранить объект в базы данных! " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение NotFoundObjectInDbException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(NotFoundObjectInDbException.class)
    public ResponseEntity<String> handlerNotFoundObjectInDbException(NotFoundObjectInDbException ex)
    {
        logger.info("Ошибка: невозможно найти объект в базе данных! " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: невозможно найти объект в базы данных! " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение FailUpdateException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(FailUpdateException.class)
    public ResponseEntity<String> handlerFailUpdateException(FailUpdateException ex)
    {
        logger.info("Ошибка: невозможно обновить объект в базе данных! " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: невозможно обновить объект в базы данных! " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение MessageFailProccessingException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом NOT_FOUND
     */
    @ExceptionHandler(FailedResponseException.class)
    public ResponseEntity<String> handlerFailedResponseException(FailedResponseException ex)
    {
        logger.info("Ошибка получения ответа от стороннего сервиса: " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка получения ответа от стороннего сервиса: " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение MassageNotRecognizedException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом BAD_REQUEST
     */
    @ExceptionHandler(MassageNotRecognizedException.class)
    public ResponseEntity<String> handlerMassageNotRecognizedException(MassageNotRecognizedException ex)
    {
        logger.info("Ошибка: содержимое сообщение не распознано!: " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: содержимое сообщение не распознано!: " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение IncorrectCurrencyCode.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом BAD_REQUEST
     */
    @ExceptionHandler(IncorrectCurrencyCode.class)
    public ResponseEntity<String> handlerIncorrectCurrencyCode(IncorrectCurrencyCode ex)
    {
        logger.info("Ошибка: Некорректный код валюты!: " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: Некорректный код валюты! " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение NotSuccessCcreateObjectException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(NotSuccessCcreateObjectException.class)
    public ResponseEntity<String> handlerNotSuccessCcreateObjectException(NotSuccessCcreateObjectException ex)
    {
        logger.info("Ошибка: Невозможно создание объекта!: " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: Невозможно создание объекта! " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение FaileMappinhToJSON.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(FaileMappinhToJSON.class)
    public ResponseEntity<String> handlerFaileMappinhToJSON(FaileMappinhToJSON ex)
    {
        logger.info("Ошибка: Невозможно преобразование объекта в JSON!: " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: Невозможно преобразование объекта в JSON! " + ex.getMessage());
    }

    /**
     * Обрабатывает исключение FailResponseFromApiException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(FailResponseFromApiException.class)
    public ResponseEntity<String> handlerFailResponseFromApiException(FailResponseFromApiException ex)
    {
        logger.info("Ошибка: Невозможно получить объекта в от стороннего API!: " + ex.getMessage());
        return  ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: Невозможно получить объекта в от стороннего API! " + ex.getMessage());
    }
    //endRegion
}

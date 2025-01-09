package com.example.servicecurrency.exceptions.globalException;

import com.example.servicecurrency.exceptions.customExceptions.FailTransformException;
import com.example.servicecurrency.exceptions.customExceptions.NullOrEmptyArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений, предназначенный для обработки различных исключений, возникающих
 * в приложении. Реализует централизованное управление ошибками, возвращая соответствующий HTTP-статус
 * и сообщение об ошибке в ответ клиенту.
 *
 * Основные особенности:
 * - Логирование ошибок с использованием {@link Logger}.
 * - Возвращает корректные HTTP-статусы, соответствующие типу возникшей ошибки.
 * - Упрощает отладку и обработку ошибок на стороне клиента.
 *
 * Аннотация:
 * - {@link ControllerAdvice}: Указывает, что этот класс обрабатывает исключения на глобальном уровне
 *   для всех контроллеров приложения.
 *
 * Методы:
 * - {@link #handleNullOrEmptyArgumentException(NullOrEmptyArgumentException)}:
 *   Обрабатывает исключение {@link NullOrEmptyArgumentException}, возвращая статус BAD_REQUEST.
 * - {@link #handleFailTransformExeption(FailTransformException)}:
 *   Обрабатывает исключение {@link FailTransformException}, возвращая статус CONFLICT.
 **/
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
     * @return ResponseEntity с сообщением об ошибке и статусом BAD_REQUEST
     */
    @ExceptionHandler(NullOrEmptyArgumentException.class) // Аннотация указывает, что этот метод обрабатывает NullOrEmptyArgumentException
    public ResponseEntity<String> handleNullOrEmptyArgumentException(NullOrEmptyArgumentException ex)
    {
        logger.info("Ошибка: Пустой или равный нулю аргумент!: {}" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка - пустой или равный нулю аргумент! : " + ex.getMessage()); // Возвращает статус BAD_REQUEST с сообщением
    }

    /**
     * Обрабатывает исключение FailTransformException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(FailTransformException.class)
    public ResponseEntity<String> handleFailTransformExeption(FailTransformException ex)
    {
        logger.info(("Ошибка: объект не может быть трансформирован в другой объект! "));

        // Возвращает статус CONFLICT с сообщением о том, что объект не может быть трансформирован в другой объект
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: объект не может быть трансформирован в другой объект! " + ex.getMessage());
    }

}


package com.example.kafkaserver.models;

import org.springframework.stereotype.Component;

/**
 * Класс ResponseResult:
 *
 * Представляет результат выполнения запроса, содержащий информацию об успехе,
 * сообщении об ошибке и данных, которые нужно вернуть.
 *
 * Поля:
 * - {@link #success}: Флаг, указывающий, был ли запрос успешным.
 * - {@link #errorMessage}: Сообщение об ошибке или сообщение об успешном выполнении.
 * - {@link #processedRequest}: Данные, возвращаемые в результате выполнения запроса.
 */
@Component
public class ResponseResult
{
    private boolean success; // Флаг успешности выполнения
    private String errorMessage; // Сообщение об ошибке или успешном выполнении
    private ProcessedRequest processedRequest;

    //region Constructors
    /**
     * Конструктор ResponseResult:
     * Инициализирует объект ResponseResult с заданными параметрами.
     *
     * @param success флаг успешности выполнения запроса.
     * @param errorMessage сообщение об ошибке или успешном выполнении.
     * @param processedRequest данные, возвращаемые в результате выполнения запроса.
     */
    public ResponseResult(boolean success, String errorMessage, ProcessedRequest processedRequest)
    {
        this.success = success;
        this.errorMessage = errorMessage;
        this.processedRequest = processedRequest;
    }

    /**
     * Конструктор по умолчанию ResponseResult:
     * Инициализирует объект ResponseResult без параметров.
     */
    public ResponseResult()
    {
        // Конструктор по умолчанию
    }
    //endRegion

    //region Getters
    /**
     * Метод isSuccess:
     * Проверяет, был ли запрос успешным.
     *
     * @return true, если запрос успешен; false в противном случае.
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     * Метод getErrorMessage:
     * Возвращает сообщение об ошибке или сообщение об успешном выполнении.
     *
     * @return сообщение об ошибке.
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Метод getProcessedRequest:
     * Возвращает данные, которые были обработаны в результате запроса.
     *
     * @return объект ProcessedRequest с данными.
     */
    public ProcessedRequest getProcessedRequest()
    {
        return processedRequest;
    }
    //endRegion

    //region Setters
    /**
     * Метод setSuccess:
     * Устанавливает флаг успешности выполнения запроса.
     *
     * @param success флаг успешности выполнения запроса.
     */
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    /**
     * Метод setErrorMessage:
     * Устанавливает сообщение об ошибке или успешном выполнении.
     *
     * @param errorMessage сообщение об ошибке.
     */
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    /**
     * Метод setProcessedRequest:
     * Устанавливает данные, возвращаемые в результате выполнения запроса.
     *
     * @param processedRequest объект ProcessedRequest с данными.
     */
    public void setProcessedRequest(ProcessedRequest processedRequest)
    {
        this.processedRequest = processedRequest;
    }
    //endRegion
}

package com.example.fetchcurrencyservice.services.requestIdService;

import com.example.fetchcurrencyservice.exceptions.customExceptions.FailSaveException;
import com.example.fetchcurrencyservice.exceptions.customExceptions.NotSuccessCcreateObjectException;
import com.example.fetchcurrencyservice.exceptions.customExceptions.NullOrEmptyArgumentException;
import com.example.fetchcurrencyservice.models.RequestIdNumber;
import com.example.fetchcurrencyservice.repository.RequestIdRepository;
import com.example.fetchcurrencyservice.services.kafkaService.CurrencyKafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.Date;

/**
 * Сервис RequestIdService:
 *
 * Обеспечивает управление идентификаторами запросов, включая их сохранение и проверку существования.
 * Этот класс взаимодействует с репозиторием для хранения идентификаторов запросов и управления
 * их сроками действия.
 *
 * Поля:
 * - {@link #logger}: Логгер для ведения журнала событий и ошибок.
 * - {@link #requestIdRepository}: Репозиторий для работы с идентификаторами запросов.
 * - {@link #requestIdNumber}: Объект, представляющий идентификатор запроса и его дату истечения.
 *
 * Конструкторы:
 * - {@link #RequestIdService(RequestIdRepository, RequestIdNumber)}:
 *   Конструктор, который инициализирует репозиторий и объект идентификатора запроса.
 *
 * Методы:
 * - {@link #saveRequestId(String, Date)}: Сохраняет идентификатор запроса с указанной датой истечения.
 * - @param requestId Идентификатор запроса, который нужно сохранить. Не должен быть null или пустым.
 * - @param expirationDate Дата истечения идентификатора запроса. Не должна быть null.
 *   @throws NullOrEmptyArgumentException если один из аргументов равен null или пустой строкой.
 *   @throws NotSuccessCcreateObjectException если не удалось создать объект RequestIdNumber.
 *   @throws FailSaveException если не удалось сохранить объект в базе данных.
 *
 * - {@link #isRequestIdExist(String)}: Проверяет, существует ли идентификатор запроса в репозитории.
 * - @param requestId Идентификатор запроса, который нужно проверить. Не должен быть null или пустым.
 *   @return true, если идентификатор запроса существует, иначе false.
 *   @throws NullOrEmptyArgumentException если аргумент requestId равен null или пустой строкой.
 *
 * - {@link #createExpirationDate()}: Создает дату истечения, которая будет на 10 дней позже текущей даты.
 *   @return Дата истечения, установленная на 10 дней позже текущей даты.
 */
@Service
public class RequestIdService
{
    //region Fields
    private static final Logger logger =  LoggerFactory.getLogger(CurrencyKafkaListener.class); // логирование
    private RequestIdRepository requestIdRepository;
    private RequestIdNumber requestIdNumber;
    //endRegion

    //region Constructor
    public RequestIdService(RequestIdRepository requestIdRepository, RequestIdNumber requestIdNumber)
    {
        this.requestIdRepository = requestIdRepository;
        this.requestIdNumber = requestIdNumber;
    }
    //endRegion


    //region Methods
    /**
     /** Метод saveRequestId:
     * Сохраняет идентификатор запроса с указанной датой истечения.
     *
     * @param requestId      Идентификатор запроса, который нужно сохранить. Не должен быть null или пустым.
     * @param expirationDate Дата истечения идентификатора запроса. Не должна быть null.
     * @throws NullOrEmptyArgumentException Если один из аргументов равен null или пустой строкой.
     * @throws NotSuccessCcreateObjectException Если не удалось создать объект RequestIdNumber.
     * @throws FailSaveException Если не удалось сохранить объект в базе данных.
     */
    public void saveRequestId(String requestId, Date expirationDate)
    {
        // Проверяем, является ли requestId null или пустой строкой
        if (requestId == null || requestId.isEmpty())
        {
            logger.warn(requestId + " равен null"); // Логируем предупреждение о том, что requestId равен null
            throw new NullOrEmptyArgumentException(requestId + " равен null"); // Генерируем исключение
        }

        // Проверяем, является ли expirationDate null
        if (expirationDate == null)
        {
            logger.warn(expirationDate + " равен null"); // Логируем предупреждение о том, что expirationDate равен null
            throw new NullOrEmptyArgumentException(expirationDate + " равен null"); // Генерируем исключение
        }

        try
        {
            // Создаем новый объект RequestIdNumber с переданными аргументами
            requestIdNumber = new RequestIdNumber(requestId, expirationDate);

            // Сохраняем объект в репозитории
            requestIdRepository.save(requestIdNumber);
        }
        catch (NotSuccessCcreateObjectException ex) // Обрабатываем исключение, если создание объекта не удалось
        {
            logger.error("Ошибка: невозможность, по каким-либо причинам, создания объекта RequestIdNumber"); // Логируем ошибку
            throw ex; // Пробрасываем исключение дальше
        }
        catch (FailSaveException ex) // Обрабатываем исключение, если сохранение объекта не удалось
        {
            logger.error("Ошибка: невозможность сохранения объекта в базе данных"); // Логируем ошибку
            throw ex; // Пробрасываем исключение дальше
        }
    }

    /**
     * Метод isRequestIdExist:
     * Проверяет, существует ли идентификатор запроса в репозитории.
     *
     * @param requestId Идентификатор запроса, который нужно проверить. Не должен быть null или пустым.
     * @return true, если идентификатор запроса существует, иначе false.
     * @throws NullOrEmptyArgumentException Если аргумент requestId равен null или пустой строкой.
     */
    public Boolean isRequestIdExist(String requestId)
    {
        // Проверяем, является ли requestId null или пустой строкой
        if (requestId == null || requestId.isEmpty())
        {
            logger.warn(requestId + " равен null"); // Логируем предупреждение о том, что requestId равен null
            throw new NullOrEmptyArgumentException(requestId + " равен null"); // Генерируем исключение
        }

        // Проверяем существование идентификатора запроса в репозитории
        return requestIdRepository.existsByRequestId(requestId);
    }

    /**
     * Метод createExpirationDate:
     * Создает дату истечения, которая будет на 10 дней позже текущей даты.
     *
     * @return Дата истечения, установленная на 10 дней позже текущей даты.
     */
    public Date createExpirationDate()
    {
        // Установка expirationDate на 10 дней позже текущей даты
        Calendar calendar = Calendar.getInstance(); // Получаем экземпляр календаря
        calendar.add(Calendar.DAY_OF_MONTH, 10); // Добавляем 10 дней к текущей дате
        Date expirationDate = calendar.getTime(); // Получаем объект Date из календаря
        return expirationDate; // Возвращаем дату истечения
    }
    //endRegion
}

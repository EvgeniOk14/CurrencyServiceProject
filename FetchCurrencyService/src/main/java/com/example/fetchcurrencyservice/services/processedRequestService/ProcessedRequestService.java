package com.example.fetchcurrencyservice.services.processedRequestService;

import com.example.fetchcurrencyservice.models.RequestIdNumber;
import com.example.fetchcurrencyservice.repository.RequestIdRepository;
import com.example.fetchcurrencyservice.services.kafkaService.CurrencyKafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Класс ProcessedRequestService:
 *
 * Сервис для управления записями идентификаторов запросов в базе данных.
 * Предоставляет методы для удаления устаревших записей, срок действия которых истек.
 *
 * Зависимости:
 * - {@link RequestIdRepository}: репозиторий для работы с коллекцией записей идентификаторов запросов в базе данных.
 *
 * Поля:
 * - {@link #logger}: логгер для ведения журнала событий и ошибок.
 * - {@link #requestIdRepository}: репозиторий для взаимодействия с базой данных.
 *
 * Конструкторы:
 * - {@link #ProcessedRequestService(RequestIdRepository)}: Конструктор, который инициализирует {@link #requestIdRepository}.
 */
@Service
public class ProcessedRequestService
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CurrencyKafkaListener.class);
    private RequestIdRepository requestIdRepository;
    //endRegion

    //region Constructor
    /**
     * Конструктор ProcessedRequestService:
     * Инициализирует репозиторий для работы с идентификаторами запросов.
     *
     * @param requestIdRepository репозиторий для работы с базой данных.
     */
    public ProcessedRequestService(RequestIdRepository requestIdRepository)
    {
        this.requestIdRepository = requestIdRepository;
    }
    //endRegion

    //region Methods
    /**
     * Метод deleteRecordsOlderThanExpirationDate:
     * Удаляет записи, срок действия которых истек.
     * Записи считаются истекшими, если дата истечения меньше текущей даты.
     */
    public void deleteRecordsOlderThanExpirationDate()
    {
        Date currentDate  =  new Date(); // устанавливаем текущую дату

        List<RequestIdNumber> allRequests = requestIdRepository.findAll(); // Получаем все записи из базы данных

        // Перебираем все записи и удаляем те, которые истекли:
        for (RequestIdNumber request : allRequests)
        {
            if (request.getExpirationDate().before(currentDate))
            {
                requestIdRepository.delete(request); // Удаляем запись из репозитория
            }
        }
        logger.info("Удалено записей с истекшим сроком: {}", allRequests.size()); // Логируем количество удаленных записей
    }

    /**
     * Метод deleteOldRecordsOlderThanDays:
     * Удаляет записи, срок действия которых истек более указанного числа дней назад.
     *
     * @param days Количество дней для проверки истечения срока действия.
     */
    public void deleteOldRecordsOlderThanDays(int days)
    {
        // Вычисляем дату, которая будет границей для удаления:
        Calendar calendar = Calendar.getInstance(); // создаём новый объект класса Calendar, который представляет собой абстракцию для работы с датами и временем в Java. getInstance() - статический метод получающий текущую дату и время в системе
        calendar.add(Calendar.DAY_OF_YEAR, -days); // Вычитаем указанное количество дней
        Date expirationThreshold = calendar.getTime(); // Получаем дату истечения

        List<RequestIdNumber> allRequests = requestIdRepository.findAll(); // Получаем все записи из базы данных

        // Перебираем все записи и удаляем те, которые истекли:
        for (RequestIdNumber request : allRequests)
        {
            if (request.getExpirationDate().before(expirationThreshold))
            {
                requestIdRepository.delete(request); // Удаляем запись из репозитория
            }
        }
        logger.info("Удалено записей с истекшим сроком: {}", allRequests.size()); // Логируем количество удаленных записей
    }
    //endRegion
}



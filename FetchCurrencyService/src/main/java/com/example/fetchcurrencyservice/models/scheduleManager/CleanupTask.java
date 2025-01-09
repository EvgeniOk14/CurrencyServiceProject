package com.example.fetchcurrencyservice.models.scheduleManager;

import com.example.fetchcurrencyservice.services.processedRequestService.ProcessedRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Класс CleanupTask:
 *
 * Задача для очистки устаревших записей, которая выполняется по расписанию.
 * Этот класс содержит методы, которые запускаются каждый день в полночь
 * для удаления устаревших записей из системы.
 *
 * Методы:
 * - {@link #cleanOldRecordsOlderThanExpirationDate()}: - очищает записи в коллекции "requestId-collection" в базе данных MongoDB, у которых истёкла дата expirationDate
 * - {@link #cleanOldRecordsOlderThanDays()}: - очищает записи в коллекции "requestId-collection" в базе данных MongoDB, время записи которых превышает 15 дней
 *
 * Поля:
 * - {@link #logger}: Логгер для записи информации о процессе очистки.
 * - {@link #processedRequestService}: Сервис, содержащий в себе методы для работы с очищением данных из коллекции "requestId-collection" в базе данных MongoDB
 */
@Component
public class CleanupTask
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(CleanupTask.class);
    private ProcessedRequestService processedRequestService;
    //endRegion

    //region Constructor
    public CleanupTask(ProcessedRequestService processedRequestService)
    {
        this.processedRequestService = processedRequestService;
    }
    //endRegion

    //region Methods
    /**
     * Метод cleanOldRecordsOlderThanExpirationDate:
     * Запускается по расписанию каждый день в полночь.
     * Этот метод выполняет очистку устаревших записей
     * Метод логирует завершение очистки
     */
    @Scheduled(cron = "0 0 0 * * ?") // Запуск каждый день в полночь
    public void cleanOldRecordsOlderThanExpirationDate()
    {
        processedRequestService.deleteRecordsOlderThanExpirationDate(); // Удаляем записи старше даты истечения срока expirationDate
        logger.info("Очистка устаревших записей завершена.");
    }

    /**
     * Метод cleanOldRecordsOlderThanDays:
     * Запускается по расписанию каждый день в полночь.
     * Этот метод выполняет очистку всех устаревших записей старше 15 дней
     * Метод логирует завершение очистки
     */
    @Scheduled(cron = "0 0 0 * * ?") // Запуск каждый день в полночь
    public void cleanOldRecordsOlderThanDays()
    {
        processedRequestService.deleteOldRecordsOlderThanDays(15); // Удаляем записи старше 15 дней
        logger.info("Очистка устаревших записей завершена.");
    }
    //endRegion
}

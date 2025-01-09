package com.example.kafkaserver.services.executorService;

import com.example.kafkaserver.interfacies.TaskExecutor;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Класс TaskExecutorImpl:
 *
 * Реализация интерфейса TaskExecutor, который предоставляет механизм
 * для выполнения задач в пуле потоков. Этот класс использует ExecutorService
 * для управления потоками.
 *
 * Поля:
 * - {@link #executorService}: Пул потоков для выполнения задач.
 */
@Service
public class TaskExecutorImpl implements TaskExecutor
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorImpl.class); // Логгер для обработки информации и ошибок
    private final ExecutorService executorService; // Пул потоков для выполнения задач
    //endRegion

    //region Constructor
    /**
     * Конструктор TaskExecutorImpl:
     * Инициализирует TaskExecutorImpl с заданным пулом потоков.
     *
     * @param executorService пул потоков для выполнения задач.
     */
    public TaskExecutorImpl(ExecutorService executorService)
    {
        this.executorService = executorService;
    }
    //endRegion

    //region Methods
    /**
     * Метод submitTask:
     * Отправляет задачу на выполнение в пул потоков.
     * Если задача отклонена из-за перегруженности пула, логирует ошибку
     * и отправляет сообщение в Kafka.
     *
     * @param task задача, которую необходимо выполнить. Должна реализовывать интерфейс Runnable.
     */
    @Override
    public void submitTask(Runnable task)
    {
        try
        {
            executorService.submit(task);
        }
        catch (RejectedExecutionException ex)
        {
            logger.error("Задача отклонена: пул потоков перегружен. Причина: {}", ex.getMessage());
        }
    }

    /**
     * Метод shutdown:
     * Завершает работу пула потоков, ожидая завершения всех задач.
     * Если задачи не завершаются в течение 60 секунд, принудительно завершает работу.
     */
    @PreDestroy
    public void shutdown()
    {
        executorService.shutdown(); // Завершаем работу пула потоков
        try
        {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) { // Ожидаем завершения в течение 60 секунд
                executorService.shutdownNow(); // Принудительно завершаем работу
            }
        }
        catch (InterruptedException e)
        {
            executorService.shutdownNow(); // Принудительно завершаем работу при прерывании
        }
    }
    //endRegion
}

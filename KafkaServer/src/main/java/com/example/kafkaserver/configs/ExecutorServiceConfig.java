package com.example.kafkaserver.configs;

import com.example.kafkaserver.interfacies.TaskExecutor;
import com.example.kafkaserver.services.executorService.TaskExecutorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

/**
 * Класс ExecutorServiceConfig:
 *
 * Конфигурационный класс, который настраивает пул потоков для обработки задач
 * в приложении. Он создает экземпляр ThreadPoolExecutor и настраивает параметры,
 * такие как минимальное и максимальное количество потоков, время ожидания и политика
 * отказа. Также реализует мониторинг состояния пула потоков.
 *
 * Методы:
 * - {@link #executorService()}: Создает и настраивает экземпляр ThreadPoolExecutor.
 * - {@link #startThreadPoolMonitoring(ThreadPoolExecutor)}: Запускает мониторинг состояния пула потоков.
 */
@Configuration
public class ExecutorServiceConfig
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceConfig.class);
    //endRegion

    //region Methods
    /**
     * Метод executorService:
     * Создает и настраивает экземпляр ThreadPoolExecutor с заданными параметрами.
     * Пул потоков будет использоваться для выполнения асинхронных задач в приложении.
     *
     * @return экземпляр ExecutorService, который управляет пулом потоков.
     */
    @Bean
    public ExecutorService executorService()
    {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                5, // Минимальное количество потоков
                20, // Максимальное количество потоков
                60, TimeUnit.SECONDS, // Время ожидания перед завершением неактивных потоков
                new LinkedBlockingQueue<>(500), // Очередь задач
                new ThreadPoolExecutor.AbortPolicy() // Политика отказа от новых задач при перегрузке
        );
        startThreadPoolMonitoring(executorService);
        return executorService;
    }

    /**
     * Метод taskExecutor:
     * Создает экземпляр TaskExecutor, который будет использовать
     * предоставленный ExecutorService для выполнения задач и взаимодействия с Kafka.
     *
     * @param executorService экземпляр ExecutorService для выполнения задач.
     * @return экземпляр TaskExecutor, который использует указанный ExecutorService.
     */
    @Bean
    public TaskExecutor taskExecutor(ExecutorService executorService)
    {
        return new TaskExecutorImpl(executorService);
    }

    /**
     * Метод startThreadPoolMonitoring:
     * Запускает мониторинг состояния пула потоков, который выводит информацию о
     * текущем количестве активных потоков, размере пула, размере очереди и количестве задач.
     *
     * @param executorService экземпляр ThreadPoolExecutor, который необходимо мониторить.
     */
    private void startThreadPoolMonitoring(ThreadPoolExecutor executorService)
    {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            logger.info(String.format(
                    "Мониторинг пула потоков: ActiveThreads: %d, PoolSize: %d, QueueSize: %d, " +
                            "CompletedTasks: %d, TotalTasks: %d",
                    executorService.getActiveCount(),
                    executorService.getPoolSize(),
                    executorService.getQueue().size(),
                    executorService.getCompletedTaskCount(),
                    executorService.getTaskCount()
            ));
        }, 0, 30, TimeUnit.SECONDS);
    }
    //endRegion
}
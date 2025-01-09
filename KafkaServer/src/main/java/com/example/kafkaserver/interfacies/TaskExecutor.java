package com.example.kafkaserver.interfacies;

/**
 * Интерфейс TaskExecutor:
 *
 * Определяет контракт для выполнения задач в виде Runnable.
 * Реализации этого интерфейса должны предоставлять механизм
 * для отправки задач на выполнение.
 *
 * Методы:
 * - {@link #submitTask(Runnable)}: Отправляет задачу на выполнение.
 */
public interface TaskExecutor
{
    /**
     * Метод submitTask:
     * Отправляет задачу на выполнение.
     *
     * @param task задача, которую необходимо выполнить. Должна реализовывать интерфейс Runnable.
     */
    void submitTask(Runnable task);
}

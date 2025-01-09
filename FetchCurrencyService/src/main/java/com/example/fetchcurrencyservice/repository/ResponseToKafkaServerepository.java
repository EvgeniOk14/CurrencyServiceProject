package com.example.fetchcurrencyservice.repository;

import com.example.fetchcurrencyservice.models.ResponseToKafkaServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс ResponseToKafkaServerRepository:
 *
 * Расширяет интерфейс JpaRepository и предоставляет методы для взаимодействия с сущностью
 * ResponseToKafkaServer в базе данных. Этот интерфейс позволяет выполнять стандартные операции
 * CRUD, а также определенные запросы, специфичные для ответов от Kafka-сервера.
 *
 * Методы:
 * - {@link #findByCurrencyWithRates(String)}: Находит объект ResponseToKafkaServer по указанной валюте
 *   и загружает связанные курсы валют (rates), возвращая заполненный объект.
 *
 * Аннотации:
 * - @Repository: Указывает, что интерфейс является репозиторием Spring, который будет управляться контейнером.
 * - @Query: Позволяет задавать JPQL-запросы для получения данных из базы с использованием JOIN FETCH.
 * - @Param: Указывает параметры, используемые в JPQL-запросах.
 */
@Repository
public interface ResponseToKafkaServerepository extends JpaRepository<ResponseToKafkaServer, Long>
{
    @Query("SELECT r FROM ResponseToKafkaServer r JOIN FETCH r.rates WHERE r.currency = :currency")
    ResponseToKafkaServer findByCurrencyWithRates(@Param("currency") String currency); // вернёт объект ResponseToKafkaServer, с заполненными полями их обеих связанных таблиц
}

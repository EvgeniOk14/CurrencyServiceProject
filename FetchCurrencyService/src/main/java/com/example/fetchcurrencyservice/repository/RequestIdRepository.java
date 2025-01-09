package com.example.fetchcurrencyservice.repository;

import com.example.fetchcurrencyservice.models.RequestIdNumber;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * Интерфейс RequestIdRepository:
 *
 * Расширяет интерфейс MongoRepository и предоставляет методы для взаимодействия с сущностью
 * RequestIdNumber в базе данных MongoDB. Этот интерфейс позволяет выполнять стандартные операции
 * CRUD, а также специфичные запросы, связанные с идентификаторами запросов.
 *
 * Методы:
 * - {@link #save(RequestIdNumber)}: Сохраняет или обновляет переданный объект RequestIdNumber в базе данных.
 * - {@link #findAll()}: Возвращает список всех объектов RequestIdNumber, хранящихся в коллекции.
 * - {@link #existsByRequestId(String)}: Проверяет, существует ли запись с указанным идентификатором запроса.
 *
 * Аннотации:
 * - @Repository: Указывает, что интерфейс является репозиторием Spring, который будет управляться контейнером.
 */
public interface RequestIdRepository extends MongoRepository<RequestIdNumber, String>
{
    RequestIdNumber save(RequestIdNumber requestIdNumber);

    List<RequestIdNumber> findAll();

    boolean existsByRequestId(String requestId);

}

package com.example.fetchcurrencyservice.repository;

import com.example.fetchcurrencyservice.models.PayloadOfMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

/**
 * Интерфейс PayloadOfMessageRepository:
 *
 * Расширяет интерфейс JpaRepository и предоставляет методы для взаимодействия с сущностью
 * PayloadOfMessage в базе данных. Этот интерфейс позволяет выполнять стандартные операции
 * CRUD, а также определенные запросы, специфичные для полезной нагрузки.
 *
 * Методы:
 * - {@link #save(PayloadOfMessage)}: Сохраняет или обновляет переданную полезную нагрузку в базе данных.
 * - {@link #existsByPayLoad(String)}: Проверяет, существует ли запись с указанной полезной нагрузкой.
 * - {@link #getPayloadOfMessageByPayLoad(String)}: Получает полезную нагрузку по ее значению.
 * - {@link #getLastSavePayloadOfMessageByPayLoad(String)}: Получает дату последнего сохранения полезной нагрузки.
 * - {@link #updateLastSavePayload(Date, String)}: Обновляет дату последнего сохранения полезной нагрузки по значению полезной нагрузки.
 *
 * Аннотации:
 * - @Repository: Указывает, что интерфейс является репозиторием Spring, который будет управляться контейнером.
 * - @Query: Позволяет задавать JPQL-запросы для получения данных из базы.
 * - @Modifying: Указывает, что метод выполняет модифицирующий запрос (например, обновление).
 * - @Transactional: Указывает, что метод должен выполняться в рамках транзакции.
 * - @Param: Указывает параметры, используемые в JPQL-запросах.
 */
@Repository
public interface PayloadOfMessageRepository extends JpaRepository<PayloadOfMessage, Long>
{
    PayloadOfMessage save(PayloadOfMessage payload);

    // Метод для проверки существования записи по полезной нагрузке
    boolean existsByPayLoad(String payLoad);
    PayloadOfMessage getPayloadOfMessageByPayLoad(String payLoad);

    // Метод для получения даты последнего сохранения полезной нагрузки
    @Query("SELECT p.lastSavePayload FROM PayloadOfMessage p WHERE p.payLoad = ?1")
    Date getLastSavePayloadOfMessageByPayLoad(String payLoad);

    @Modifying
    @Transactional
    @Query("UPDATE PayloadOfMessage p SET p.lastSavePayload = :lastSavePayload WHERE p.payLoad = :payLoad")
    void updateLastSavePayload(@Param("lastSavePayload") Date lastSavePayload, @Param("payLoad") String payLoad);
}

package com.example.requestcurrecyservice.configs;

import com.example.requestcurrecyservice.services.kafkaService.CurrencyKafkaListener;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс KafkaConfig:
 *
 * Конфигурационный класс для настройки Kafka в приложении. Содержит настройки для Producer и Consumer,
 * а также настройки для обработки повторных попыток при неудаче и создания необходимых топиков.
 *
 * Поля:
 * - KAFKA_BROKER: адрес Kafka брокера.
 * - GROUP_ID: группа Consumer'ов.
 * - logger: логгер для записи информации о конфигурации.
 * - maxAttempts: максимальное количество попыток для операций.
 * - backOffPeriod: задержка между попытками.
 *
 * Методы:
 * - {@link #producerConfigs()}: Возвращает настройки для Kafka Producer в виде Map.
 * - {@link #kafkaTemplate()}: Создает и настраивает экземпляр KafkaTemplate для отправки сообщений.
 * - {@link #producerFactory()}: Создает и настраивает фабрику Producer'ов.
 * - {@link #consumerConfigs()}: Возвращает настройки для Kafka Consumer в виде Map.
 * - {@link #consumerFactory()}: Создает и настраивает фабрику Consumer'ов.
 * - {@link #retryTemplate()}: Создает и настраивает экземпляр RetryTemplate для обработки повторных попыток.
 * - {@link #fetchCurrencyTopic()}: Создает и настраивает топик для получения курсов валют.
 * - {@link #requestCurrencyTopic()}: Создает и настраивает топик для запросов к курсам валют.
 * - {@link #responseTopic()}: Создает и настраивает топик для отправки ответов с курсами валют.
 * - {@link #deadLetterTopic()}: Создает и настраивает топик для мертвых писем (Dead Letter Topic).
 *
 * Аннотации:
 * - @EnableKafka: Включает поддержку Kafka в приложении.
 * - @Configuration: Указывает, что класс является конфигурационным и управляется Spring-контейнером.
 */
@EnableKafka
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaConfig
{
    //region Fields
    private static final String KAFKA_BROKER = "localhost:9092"; // Адрес Kafka брокера
    private static final String GROUP_ID = "kafka-server-group"; // Группа Consumer'ов
    private static final Logger logger =  LoggerFactory.getLogger(CurrencyKafkaListener.class); // логирование

    @Value("${spring.kafka.producer.transactional-id}")
    private String transactionalId; // Получаем transactional-id из конфигурации

    @Value("${spring.kafka.retry.max-attempts}")
    private int maxAttempts;  // Чтение из конфигурации максимального числа попыток

    @Value("${spring.kafka.retry.backoff-period}")
    private long backOffPeriod;  // Чтение из конфигурации задержки между попытками
    //endRegion

    //region Methods
    /**
     * Настройки Producer для Kafka.
     *
     * @return Map с настройками Kafka Producer.
     */
    @Bean
    public Map<String, Object> producerConfigs()
    {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return props;
    }

    /**
     * Настройка KafkaTemplate для отправки сообщений в Kafka.
     *
     * @return KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate()
    {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Фабрика Producer'ов.
     *
     * @return ProducerFactory
     */
    @Bean
    public ProducerFactory<String, String> producerFactory()
    {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * Настройки Consumer для Kafka.
     *
     * @return Map с настройками Kafka Consumer.
     */
    @Bean
    public Map<String, Object> consumerConfigs()
    {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER); // Адрес Kafka брокера
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID); // Группа Consumer'ов
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Десериализация ключа
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Десериализация значения
        return props;
    }

    /**
     * Фабрика Consumer'ов.
     *
     * @return ConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory()
    {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }


    /**
     * Создает и настраивает экземпляр {@link RetryTemplate} для обработки повторных попыток
     * в случае неудачи выполнения операций, таких как отправка сообщений в Kafka или выполнение HTTP-запросов.
     *
     * @return настроенный экземпляр {@link RetryTemplate} с заданной политикой повторных попыток (5штук)
     *         и задержкой между попытками(2секунды).
     */
    @Bean
    public RetryTemplate retryTemplate()
    {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);  // Максимальное количество попыток
        logger.info("Max attempts for retry set to: {}", maxAttempts);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(backOffPeriod); // Начальный интервал 1 секунда
        logger.info("Backoff period for retry set to: {} ms", backOffPeriod);
        backOffPolicy.setMultiplier(2.0); // Умножитель для увеличения интервала
        backOffPolicy.setMaxInterval(5000); // Максимальный интервал между попытками

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    @Bean
    public NewTopic fetchCurrencyTopic()
    {
        return TopicBuilder.name("fetch-currency-topic").build();
    }

    @Bean
    public NewTopic requestCurrencyTopic()
    {
        return TopicBuilder.name("request-currency-topic").build();
    }

    @Bean
    public NewTopic responseTopic()
    {
        return TopicBuilder.name("response-topic").build();
    }

    @Bean
    public NewTopic deadLetterTopic()
    {
        return TopicBuilder.name("dead-letter-topic").build();
    }
    //endRegion
}



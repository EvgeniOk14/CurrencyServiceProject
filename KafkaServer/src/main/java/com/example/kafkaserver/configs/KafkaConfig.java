package com.example.kafkaserver.configs;

import com.example.kafkaserver.services.KafkaListenerService;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Класс KafkaConfig:
 *
 * Конфигурационный класс для настройки Kafka в приложении.
 * Он содержит настройки для Producer и Consumer, а также для
 * управления темами и политиками повторных попыток.
 *
 * Зависимости:
 * - {@link KafkaTemplate}: Шаблон для отправки сообщений в Kafka.
 * - {@link RetryTemplate}: Шаблон для обработки повторных попыток выполнения операций.
 *
 * Методы:
 * - {@link #producerConfigs()}: Настройки Producer для Kafka.
 * - {@link #kafkaTemplate()}: Настройка KafkaTemplate для отправки сообщений.
 * - {@link #producerFactory()}: Фабрика Producer'ов.
 * - {@link #consumerConfigs()}: Настройки Consumer для Kafka.
 * - {@link #consumerFactory()}: Фабрика Consumer'ов.
 * - {@link #retryTemplate()}: Создает и настраивает экземпляр RetryTemplate.
 * - {@link #fetchCurrencyTopic()}: Создает тему для получения курсов валют.
 * - {@link #queryCurrencyTopic()}: Создает тему для запросов валют.
 * - {@link #responseTopic()}: Создает тему для ответов.
 * - {@link #errorTopic()}: Создает тему для ошибок.
 */
@Configuration
@EnableKafka
public class KafkaConfig
{
    //region Fields
    private static final String KAFKA_BROKER = "localhost:9092"; // Адрес Kafka брокера
    private static final String GROUP_ID = "kafka-server-group"; // Группа Consumer'ов
    private static final Logger logger =  LoggerFactory.getLogger(KafkaListenerService.class); // логирование

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
        Map<String, Object> props = new HashMap<>(); // объект типа Map, для хранения настроек продюсера Kafka (String — ключи, представляющие названия конфигураций и Object — значения, которые могут быть разного типа, в зависимости от конфигурации.)
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER); // BOOTSTRAP_SERVERS_CONFIG - это адрес Kafka брокера "bootstrap.servers" к которому будет подключаться Producer. И KAFKA_BROKER - это "localhost:9092"
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // Константа, указывающая на класс, который будет использоваться для Сериализация ключа перед их отправкой в Kafka и StringSerializer.class - Класс сериализатора, который преобразует ключи сообщений в строковый формат
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG для сериализация значений сообщений перед отправкой их в Kafka и StringSerializer.class - Класс сериализатора, который преобразует ключи сообщений в строковый формат
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId); //ProducerConfig.TRANSACTIONAL_ID_CONFIG Константа, указывающая на конфигурацию, необходимую для включения поддержки транзакций в Kafka Produce и transactionalId - строка, представляющая уникальный идентификатор транзакции. Чаще всего это значение должно быть уникальным для каждого Producer, чтобы Kafka мог отслеживать транзакции
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
     *
     *   - RetryTemplate — это класс из Spring Framework, который упрощает реализацию механизма повторных попыток
     *     для выполнения операций, которые могут завершиться неудачей, например, при взаимодействии с внешними сервисами
     *     или базами данных.
     */
    @Bean
    public RetryTemplate retryTemplate()
    {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();  // объект SimpleRetryPolicy определяет правила для повторных попыток
        retryPolicy.setMaxAttempts(maxAttempts);  // Максимальное количество попыток 5
        logger.info("Max attempts for retry set to: {}", maxAttempts); // логирование максимальных попыток

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy(); // объект ExponentialBackOffPolicy определяет политику ожидания между попытками.
        backOffPolicy.setInitialInterval(backOffPeriod); // Начальный интервал 2 секунды
        logger.info("Backoff period for retry set to: {} ms", backOffPeriod); // логирование интервала ожидания
        backOffPolicy.setMultiplier(2.0); // Умножитель для увеличения интервала между попытками
        backOffPolicy.setMaxInterval(5000); // Максимальный интервал между попытками

        RetryTemplate retryTemplate = new RetryTemplate(); // Создается экземпляр RetryTemplate, который будет использоваться для выполнения операций с механизмом повторных попыток.
        retryTemplate.setRetryPolicy(retryPolicy); // Устанавливаются ранее созданные политики для повторных попыток
        retryTemplate.setBackOffPolicy(backOffPolicy); // Устанавливаются ранее созданные политики для ожидания

        return retryTemplate; //  возвращает настроенный объект RetryTemplate
    }

    /**
     * Метод fetchCurrencyTopic:
     * Создает тему для получения курсов валют.
     *
     * @return новый экземпляр NewTopic для темы "fetch-currency-topic".
     */
    @Bean
    public NewTopic fetchCurrencyTopic()
    {
        return TopicBuilder.name("fetch-currency-topic").build(); // Создаем и возвращаем тему
        // можно задать количество партиций и репликаций через: TopicBuilder.name("fetch-currency-topic").partitions(3).replicas(1).build()
    }

    /**
     * Метод queryCurrencyTopic:
     * Создает тему для запросов валют.
     *
     * @return новый экземпляр NewTopic для темы "query-currency-topic".
     */
    @Bean
    public NewTopic queryCurrencyTopic()
    {
        return TopicBuilder.name("query-currency-topic").build(); // Создаем и возвращаем тему
    }

    /**
     * Метод responseTopic:
     * Создает тему для ответов.
     *
     * @return новый экземпляр NewTopic для темы "response-topic".
     */
    @Bean
    public NewTopic responseTopic()
    {
        return TopicBuilder.name("response-topic").build(); // Создаем и возвращаем тему
    }

    /**
     * Метод errorTopic:
     * Создает тему для ошибок.
     *
     * @return новый экземпляр NewTopic для темы "error-topic".
     */
    @Bean
    public NewTopic errorTopic()
    {
        return TopicBuilder.name("error-topic").build(); // Создаем и возвращаем тему
    }
    //endRegion
}
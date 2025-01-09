package com.example.requestcurrecyservice.services.currencyService;

import com.example.requestcurrecyservice.exceptions.customExceptions.FailedResponseException;
import com.example.requestcurrecyservice.exceptions.customExceptions.NullOrEmptyArgumentException;
import com.example.requestcurrecyservice.models.ExchangeRateResponseFromAPI;
import com.example.requestcurrecyservice.models.ResponseToKafkaServer;
import com.example.requestcurrecyservice.services.kafkaService.CurrencyKafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс CurrencyService:
 *
 * Реализует бизнес-логику для работы с курсами валют. Предоставляет методы для получения всех курсов валют
 * от стороннего API, а также для фильтрации курсов по заданным валютам. Класс использует RestTemplate для
 * взаимодействия с внешним API и обработки ответов.
 *
 * Зависимости:
 * - API_KEY: ключ доступа к стороннему API для получения курсов валют.
 * - logger: логгер для ведения журнала событий и ошибок.
 * - BASE_URL: базовый URL для доступа к стороннему API.
 * - RestTemplate: класс для выполнения HTTP-запросов и получения ответов в виде объектов.
 *
 * Поля:
 * - {@link #API_KEY}: Ключ для доступа к стороннему API для получения курсов валют.
 * - {@link #BASE_URL}: Базовый URL для выполнения запросов к API.
 * - {@link #restTemplate}: Объект для выполнения HTTP-запросов к RESTful веб-сервисам.
 *
 * Конструкторы:
 * - {@link #CurrencyService(RestTemplate)}: Конструктор, который инициализирует {@link #restTemplate}.
 *
 *
 * Методы:
 * - {@link #fetchAllExchangeRates()}: Получает все курсы валют от стороннего API.
 * - {@link #getExchangeRate(String)}: Получает курсы валют по заданному фильтру или все курсы, если передан "ALL".
 * - {@link #getAllExchangeRates(ExchangeRateResponseFromAPI)}: Получает все доступные курсы валют из ответа API.
 * - {@link #getFilteredExchangeRates(ExchangeRateResponseFromAPI, String)}: Получает фильтрованные курсы валют по переданному списку.
 *
 * Исключения:
 * - {@link FailedResponseException}: выбрасывается при ошибке получения ответа от стороннего API или если ответ пустой.
 * - {@link NullOrEmptyArgumentException}: выбрасывается, если переданы аргументы, равные null или пустым строкам.
 *
 * Аннотации:
 * - @Service: указывает, что класс является сервисом и управляется Spring-контейнером.
 */

@Service
public class CurrencyService
{
    //region Fields
    //private final String API_KEY = "6646194901d5ff7400ab279852158174"; //  ключ стороннего API, сайта курса валют, расположенного по адресу: "https://exchangeratesapi.io"
    //private final String API_KEY = "7bf9cc5409ae6addc26e7fa78b4356da"; //  ключ стороннего API, сайта курса валют, расположенного по адресу: "https://exchangeratesapi.io"
    private final String API_KEY = "9507ed7557c0a6c1a2ecd2d98d967f62"; //  ключ стороннего API, сайта курса валют, расположенного по адресу: "https://exchangeratesapi.io"
    private static final Logger logger = LoggerFactory.getLogger(CurrencyKafkaListener.class); // логирование
    private final String BASE_URL = "https://api.exchangeratesapi.io/v1/latest?access_key="; // базовый URL
    private RestTemplate restTemplate; // класс в Spring Framework, который предоставляет удобный способ взаимодействия с RESTful веб-сервисами
    //endRegion

    //region Constructors
    public CurrencyService(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }
    //endRegion

    //region Methods
    /**
     * Метод fetchAllExchangeRates:
     * Предназначен метод получения всех валют от стороннего API
     *
     * - @exception FailedResponseException - кастомное исключение, обрабатывающееся на глобальном уровне, в случае если: ответ от стороннего API пустой или равен null
     *
     * @return ExchangeRateResponse - объект типа ExchangeRateResponse, который содержит в себе ответ стороннего API
     *
     * **/
    public ExchangeRateResponseFromAPI fetchAllExchangeRates()
    {
        try
        {
            String url = BASE_URL + API_KEY; // формируем общий путь

            // Метод getForObject в классе RestTemplate является одним из основных методов,
            // используемых для выполнения HTTP-запросов в Spring Framework,
            // получает ответ в виде объекта желаемого типа.
            ExchangeRateResponseFromAPI response = restTemplate.getForObject(url, ExchangeRateResponseFromAPI.class);

            return response; // возвращаем ответ
        }
        catch (FailedResponseException ex) // кастомное исключение, обрабатывающееся на глобальном уровне, в случае если: ответ от стороннего API пустой или равен null
        {
            throw ex; // обрабатываем на глобальном уровне
        }
    }

    /**
     * Метод getExchangeRate:
     * Метод для получения курсов валют, как одной переданной валюты, так и фильтра с валютами
     * @param currencyFilter - список кодов валют, через запятую (пример: USD, EUR, RUB)
     *
     * - @exception FailedResponseException - кастомное исключение FailedResponseException на глобальном уровне, в случае ошибки при получения курсов валют
     *
     * @return ExchangeRateResponse - объект типа ExchangeRateResponse, который содержит в себе ответ стороннего API
     */
    public ResponseToKafkaServer getExchangeRate(String currencyFilter)
    {
        if (currencyFilter == null || currencyFilter.isEmpty())
        {
            logger.error("Ошибка: передача аргумента равного null {}", currencyFilter);
            throw new FailedResponseException("Код валюты не передан в запросе");
        }

        try
        {
            ExchangeRateResponseFromAPI response = fetchAllExchangeRates(); // Получаем все курсы валют от стороннего API

            if (currencyFilter.equals("ALL")) // Если передан фильтр, проверяем на наличие "ALL"
            {
                return getAllExchangeRates(response); // возвращаем результат по всем валютам
            }
            else // Получаем  курсы валют по заданному фильтру от стороннего API
            {
                return getFilteredExchangeRates(response, currencyFilter); // возвращаем результат по фильтрам валютам
            }
        }
        catch (FailedResponseException ex)  // обрабатываем кастомное исключение FailedResponseException на глобальном уровне, в случае ошибки при получения курсов валют
        {
            logger.error("Ошибка: проблема при получения ответа");
            throw  ex;
        }
    }

    /**
     * Метод getAllExchangeRates:
     * Предназначен для получения всех доступных курсов валют.
     *
     * @param response - ответ стороннего API с курсами валют
     * @exception NullOrEmptyArgumentException - передача аргумента равного null
     * @return ResponseToKafkaServer - объект с курсами всех валют
     *
     */
    private ResponseToKafkaServer getAllExchangeRates(ExchangeRateResponseFromAPI response)
    {
        if (response == null)
        {
            logger.error("Ошибка: передача аргумента равного null {}", response);
            throw new NullOrEmptyArgumentException("Передан аргумент равный null");
        }
        // Возвращаем все доступные курсы, если передан "ALL"
        return new ResponseToKafkaServer(response.getRates(), response.getBase(), response.getDate(), "ALL", null);
    }

    /**
     * Метод getFilteredExchangeRates:
     * Предназначен для получения фильтрованных курсов валют по переданному списку.
     *
     * @param response - ответ стороннего API с курсами валют
     * @param currencyFilter - список кодов валют, через запятую
     *
     * @exception NullOrEmptyArgumentException - передача аргумента равного null
     *
     * @return ResponseToKafkaServer - объект с фильтрованными курсами валют
     *
     */
    private ResponseToKafkaServer getFilteredExchangeRates(ExchangeRateResponseFromAPI response, String currencyFilter)
    {
        if (response == null)
        {
            logger.error("Ошибка: передача аргумента равного null {}", response);
            throw new NullOrEmptyArgumentException("Передан аргумент равный null");
        }
        if (currencyFilter == null)
        {
            logger.error("Ошибка: передача аргумента равного null {}", currencyFilter);
            throw new NullOrEmptyArgumentException("Передан аргумент равный null");
        }

        // Разделяем фильтр на отдельные валюты
        String[] currencies = currencyFilter.split(",");
        Map<String, Double> filteredRates = new HashMap<>();

        // Проходим по каждой валюте в фильтре
        for (String currency : currencies)
        {
            String trimmedCurrency = currency.trim(); // Убираем лишние пробелы
            if (response.getRates().containsKey(trimmedCurrency))
            {
                filteredRates.put(trimmedCurrency, response.getRates().get(trimmedCurrency)); // формируем фильтр с ответом по кодам и курсам валют
            }
            else
            {
                logger.error("Курс для валюты {} не найден", trimmedCurrency);
                throw new FailedResponseException("Курс для валюты " + trimmedCurrency + " не найден");
            }
        }
        // Формируем ответ с фильтрованными курсами
        return new ResponseToKafkaServer(filteredRates, response.getBase(), response.getDate(), currencyFilter, null);
    }
    //endRegion
}

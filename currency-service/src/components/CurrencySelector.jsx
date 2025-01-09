import React, { useState } from 'react';
import axios from 'axios';
import ResponseTable from './ResponseTable';  // Импортируем компонент
import '../css/CurrencySelector.css';

const CurrencySelector = () => {
    const [selectedCurrencies, setSelectedCurrencies] = useState([]);
    const [singleCurrency, setSingleCurrency] = useState('');
    const [responseData, setResponseData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

        const currencyCodes = [
        "FJD", "MXN", "STD", "LVL", "SCR", "CDF", "BBD", "HNL", "UGX", "ZAR",
        "CUC", "BSD", "SDG", "IQD", "CUP", "GMD", "TWD", "RSD", "MYR", "FKP",
        "XOF", "BTC", "UYU", "CVE", "OMR", "KES", "SEK", "BTN", "GNF", "MZN",
        "SVC", "ARS", "QAR", "IRR", "THB", "UZS", "XPF", "BDT", "LYD", "KWD",
        "RUB", "ISK", "MKD", "DZD", "PAB", "SGD", "JEP", "KGS", "XAF", "XAG",
        "CHF", "HRK", "DJF", "TZS", "VND", "XAU", "AUD", "KHR", "IDR", "KYD",
        "BWP", "SHP", "TJS", "AED", "RWF", "DKK", "BGN", "MMK", "NOK", "SYP",
        "ZWL", "LKR", "CZK", "XCD", "HTG", "BHD", "KZT", "SZL", "YER", "AFN",
        "AWG", "NPR", "MNT", "GBP", "BYN", "HUF", "BYR", "BIF", "XDR", "BZD",
        "MOP", "NAD", "PEN", "WST", "TMT", "CLF", "GTQ", "CLP", "TND", "SLE",
        "SLL", "DOP", "KMF", "GEL", "MAD", "TOP", "AZN", "PGK", "CNH", "UAH",
        "ERN", "CNY", "MRU", "BMD", "PHP", "PYG", "JMD", "COP", "USD", "GGP",
        "ETB", "SOS", "VUV", "LAK", "BND", "ZMK", "LRD", "ALL", "VES", "ZMW",
        "ILS", "GHS", "GYD", "KPW", "BOB", "MDL", "AMD", "TRY", "LBP", "JOD",
        "HKD", "EUR", "LSL", "CAD", "MUR", "IMP", "GIP", "RON", "NGN", "CRC",
        "PKR", "ANG", "SRD", "LTL", "SAR", "TTD", "MVR", "INR", "KRW", "JPY",
        "AOA", "PLN", "SBD", "MWK", "MGA", "BAM", "EGP", "NIO", "NZD", "BRL"
    ];

    const handleCheckboxChange = (currency) => {
        setSelectedCurrencies(prevSelected => {
            if (prevSelected.includes(currency)) {
                return prevSelected.filter(item => item !== currency);
            } else {
                return [...prevSelected, currency];
            }
        });
    };

    const handleRequest = (type, params = '') => {
        setLoading(true);
        setError(null);

        const urlMap = {
            ALL: 'http://localhost:8080/api/gateway/currencies/all',
            FILTER: `http://localhost:8080/api/gateway/currencies/filter/${params}`,
            SINGLE: `http://localhost:8080/api/gateway/currencies/single/${params}`,
        };

        const url = urlMap[type];
        if (!url) {
            console.error("Неверный тип запроса:", type);
            setLoading(false);
            return;
        }

        axios.get(url)
            .then(response => {
                console.log("Ответ успешно получен от сервера:", response.data);

                let data;

                // Если ответ - строка, а не JSON, пытаемся извлечь JSON из строки
                if (typeof response.data === 'string') {
                    const jsonString = response.data.substring(response.data.indexOf("{"), response.data.length);

                    try {
                        data = JSON.parse(jsonString); // Преобразуем строку в JSON
                        console.log("Parsed data:", data);
                    } catch (error) {
                        console.error("Ошибка при парсинге данных:", error);
                        setError("Получены некорректные данные от сервера");
                        return;
                    }
                } else {
                    // Если ответ уже в формате JSON
                    data = response.data;
                }

                // Теперь, data — это объект с нужной структурой
                const rates = data.rates;
                const baseCurrency = data.baseCurrency;
                const date = data.date;

                // Преобразуем данные в формат для таблицы
                const tableData = Object.entries(rates).map(([currency, rate]) => ({
                    currency,
                    rate,
                }));

                // Обновляем состояние с данными для таблицы
                setResponseData({
                    baseCurrency,
                    date,
                    tableData,
                });

                handlePostResponse(data); // Отправляем на сервер
            })
            .catch(error => {
                console.error("Ошибка при запросе:", error);
                setError("Ошибка при получении данных с сервера");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    const handlePostResponse = (responseBody) => {
        axios.post('http://localhost:8080/api/gateway/response', responseBody)
            .then(res => {
                console.log("Ответ отправлен на сервер:", res.data);
            })
            .catch(error => {
                console.error("Ошибка отправки ответа на сервер:", error);
            });
    };

    return (
        <div className="currency-selector">
            <div className="currency-selector_choise">
                <div className="filter-choise">
                    <h2>Выберите валюты</h2>
                    <div className="checkbox-container">
                        {currencyCodes.map(currency => (
                            <label key={currency}>
                                <input
                                    type="checkbox"
                                    value={currency}
                                    checked={selectedCurrencies.includes(currency)}
                                    onChange={() => handleCheckboxChange(currency)}
                                />
                                {currency}
                            </label>
                        ))}
                    </div>
                    <div className="div-filter-button">
                        <button
                            className="filter-button"
                            onClick={() => handleRequest('FILTER', selectedCurrencies.join(','))}>
                            Получить курсы валют по списку
                        </button>
                    </div>
                </div>

                <div className="single-choise">
                    <h2>Выберите одну конкретную валюту:</h2>
                    <label>
                        Выберите одну валюту:
                        <select onChange={e => setSingleCurrency(e.target.value)} value={singleCurrency}>
                            <option value="">--Выберите валюту--</option>
                            {currencyCodes.map(currency => (
                                <option key={currency} value={currency}>{currency}</option>
                            ))}
                        </select>
                    </label>
                    <button
                        className="single-button"
                        onClick={() => handleRequest('SINGLE', singleCurrency)}>
                        Получить курс выбранной валюты
                    </button>
                </div>

                <div className="all-choise">
                    <h2>Получить все доступные валюты:</h2>
                    <button
                        className="all-button"
                        onClick={() => handleRequest('ALL')}>Получить все валюты
                    </button>
                </div>
            </div>
            <div className="response-container">
                {/* Здесь мы передаем данные в компонент ResponseTable */}
                {responseData && <ResponseTable data={responseData} />}

                {/* Показать ошибку если есть */}
                {error && <p className="error">{error}</p>}

                {/* Показать загрузку если идет запрос */}
                {loading && <p>Загрузка...</p>}
            </div>
        </div>
    );
};

export default CurrencySelector;



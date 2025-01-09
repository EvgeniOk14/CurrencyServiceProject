package com.example.servicecurrency.service;

import com.example.servicecurrency.interfaces.CurrencyProcessor;
import com.example.servicecurrency.models.abstructModels.Currency;
import com.example.servicecurrency.models.realModels.Dollar;
import com.example.servicecurrency.models.realModels.Euro;
import com.example.servicecurrency.models.realModels.Ruble;
import com.example.servicecurrency.models.realModels.Yuan;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class CurrencyProcessorImpl implements CurrencyProcessor
{
    private final Map<String, Currency> currencies = Map.of(
            "USD", new Dollar(),
            "EUR", new Euro(),
            "RUB", new Ruble(),
            "CNY", new Yuan()
    );

    @Override
    public Currency getCurrency(String code)
    {
        return currencies.getOrDefault(code, null);
    }
}
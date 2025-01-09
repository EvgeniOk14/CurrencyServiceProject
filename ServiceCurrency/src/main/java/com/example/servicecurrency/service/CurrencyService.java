package com.example.servicecurrency.service;

import com.example.servicecurrency.exceptions.customExceptions.FailTransformException;
import com.example.servicecurrency.models.abstructModels.Currency;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService
{
    //region Fields

    //endRegion

    //region Constructors

    //endRegion

    //region Methods
    /**
     *
     * **/
    public Currency returnObjectCurrency(String info)
    {
        try
        {
            Currency currency = parseCurrency(info);
            return currency;
        }
        catch (FailTransformException ex) // // выбрасываем кастомное исключение, обработанное на глобальном уровне, в случае, если объект не может быть трансформирован в другой объект
        {
           throw ex;
        }

    }

    private Currency parseCurrency(String message)
    {
        try
        {
            Gson gson = new Gson();
            Currency currency = gson.fromJson(message, Currency.class);
            return currency;
        }
        catch (JsonSyntaxException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    //endRegion
}

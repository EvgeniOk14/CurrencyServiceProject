package com.example.servicecurrency.interfaces;

import com.example.servicecurrency.models.abstructModels.Currency;

public interface CurrencyProcessor
{
    Currency getCurrency(String code);
}
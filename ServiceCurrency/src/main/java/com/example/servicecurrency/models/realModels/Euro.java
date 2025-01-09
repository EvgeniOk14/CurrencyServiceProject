package com.example.servicecurrency.models.realModels;

import com.example.servicecurrency.models.abstructModels.Currency;

public class Euro extends Currency
{
    public Euro()
    {
        super("Euro", "EUR");
    }

    @Override
    public String getDescription()
    {
        return "Европейский Евро ";
    }
}
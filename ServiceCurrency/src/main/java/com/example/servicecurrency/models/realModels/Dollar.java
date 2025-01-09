package com.example.servicecurrency.models.realModels;

import com.example.servicecurrency.models.abstructModels.Currency;


public class Dollar extends Currency
{
    public Dollar()
    {
        super("Dollar", "USD");
    }

    @Override
    public String getDescription()
    {
        return "Доллар США ";
    }
}

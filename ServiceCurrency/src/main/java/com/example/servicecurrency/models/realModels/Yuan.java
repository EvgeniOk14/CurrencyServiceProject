package com.example.servicecurrency.models.realModels;

import com.example.servicecurrency.models.abstructModels.Currency;

public class Yuan extends Currency
{
    public Yuan()
    {
        super("Yuan", "Китай");
    }

    @Override
    public String getDescription()
    {
        return "Юань Китай ";
    }
}


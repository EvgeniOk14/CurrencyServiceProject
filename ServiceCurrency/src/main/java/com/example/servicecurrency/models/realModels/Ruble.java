package com.example.servicecurrency.models.realModels;


import com.example.servicecurrency.models.abstructModels.Currency;

public class Ruble extends Currency
{
    public Ruble()
    {
        super("Ruble", "Russian");
    }


    @Override
    public String getDescription() {
        return "Рубль Россия ";
    }
}

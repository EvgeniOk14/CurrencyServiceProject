package com.example.servicecurrency.models.abstructModels;


public abstract class Currency
{
    //region Fields
    protected String name;
    protected String code;
    //endRegion

    //region Constructor
    public Currency(String name, String code)
    {
        this.name = name;
        this.code = code;
    }
    //endRegion

    //region Getters
    public String getName()
    {
        return name;
    }

    public String getCode()
    {
        return code;
    }
    //endRegion

    //region Methods
    public abstract String getDescription();
    //endRegion
}

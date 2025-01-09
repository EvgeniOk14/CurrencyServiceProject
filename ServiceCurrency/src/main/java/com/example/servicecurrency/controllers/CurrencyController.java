package com.example.servicecurrency.controllers;

import com.example.servicecurrency.dto.CurrencyDTO;
import com.example.servicecurrency.interfaces.CurrencyProcessor;
import com.example.servicecurrency.models.abstructModels.Currency;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController
{
    private final CurrencyProcessor currencyProcessor;

    public CurrencyController(CurrencyProcessor currencyProcessor)
    {
        this.currencyProcessor = currencyProcessor;
    }

    @GetMapping("/{code}")
    public ResponseEntity<CurrencyDTO> getCurrency(@PathVariable String code)
    {
        Currency currency = currencyProcessor.getCurrency(code);
        if (currency == null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new CurrencyDTO(currency.getName(), currency.getCode()));
    }
}

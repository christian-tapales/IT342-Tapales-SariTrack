package edu.cit.tapales.saritrack.core.currency.controller;

import edu.cit.tapales.saritrack.core.currency.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/rates")
    public Map<String, Double> getRates() {
        return currencyService.getLatestRates();
    }
}

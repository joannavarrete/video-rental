package com.joannava.video.service;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.joannava.video.bo.Price;
import com.joannava.video.dao.Film.Type;

@Service
public class PriceCalculatorService {

    private final Map<Type, Price> prices = new TreeMap<>();

    //should be a database table.
    public PriceCalculatorService(){
        prices.put(Type.RELEASE, new Price(new BigInteger("40"), Integer.MAX_VALUE, 1));
        prices.put(Type.REGULAR, new Price(new BigInteger("30"), 3, 3));
        prices.put(Type.OLD, new Price(new BigInteger("30"), 5, 5));
    }

    //We could/should use a strategy pattern here
    public BigInteger calculate(Type type, int days) {

        var price = prices.get(type);
        BigInteger total = new BigInteger("0");

        switch (type) {
            case RELEASE:
                total = price.getDefaultPrice().multiply(BigInteger.valueOf(days));
                break;
            case OLD:
            case REGULAR:
                if (days <= price.getNDaysDefaultPrice())
                    total = price.getDefaultPrice();
                else
                    total = price.getDefaultPrice()
                            .add(price.getDefaultPrice()
                                    .divide(BigInteger.valueOf(price.getRateExtraDays()))
                                    .multiply(BigInteger.valueOf(days - price.getNDaysDefaultPrice())));

        }
        return total;
    }

}

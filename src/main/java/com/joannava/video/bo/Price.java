package com.joannava.video.bo;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Price {
    private final BigInteger defaultPrice;
    private final int nDaysDefaultPrice;
    private final int rateExtraDays;
}

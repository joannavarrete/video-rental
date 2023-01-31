package com.joannava.video.dto;

import java.math.BigInteger;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentedFilm {

    private final String id;
    private final String name;
    private final boolean errorOcurred;
    private BigInteger price;
    private String errorString;

}

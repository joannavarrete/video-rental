package com.joannava.video.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joannava.video.dao.Film;
import com.joannava.video.dao.Film.Type;
import com.joannava.video.dto.RentRequest;
import com.joannava.video.dto.RentedFilm;
import com.joannava.video.repository.FilmRepository;

import lombok.AllArgsConstructor;

@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final Map<Type, Price> prices = new TreeMap<>();

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
        startDatabase();
        startPrices();
    }

    @Transactional
    public List<RentedFilm> rent(Iterable<RentRequest> rentRequests) {

        List<RentedFilm> rented = new ArrayList<>();

        for (RentRequest request : rentRequests) {

            Optional<Film> film = filmRepository.findById(request.getId());

            if (!film.isPresent()) {
                rented.add(RentedFilm.builder()
                        .id(request.getId())
                        .errorOcurred(true)
                        .errorString("No Such Film In our Catalogue")
                        .price(new BigInteger("0"))
                        .build());

            } else if (film.get().isRented()) {
                rented.add(RentedFilm.builder()
                        .id(request.getId())
                        .errorOcurred(true)
                        .errorString("Already Rented")
                        .price(new BigInteger("0"))
                        .build());
            } else {
                var realFilm = film.get();

                rented.add(RentedFilm.builder()
                        .id(realFilm.getId())
                        .name(realFilm.getName())
                        .price(generatePrice(realFilm.getType(), request.getDays()))
                        .errorOcurred(false)
                        .build());
                realFilm.setRented(true);

                filmRepository.save(realFilm);
            }

        }

        return rented;
    }

    private BigInteger generatePrice(Type type, int days) {

        var price = prices.get(type);
        BigInteger total = new BigInteger("0");

        switch (type) {
            case RELEASE:
                total = price.defaultPrice.multiply(BigInteger.valueOf(days));
                break;
            case OLD:
            case REGULAR:
                if (days <= price.nDaysDefaultPrice)
                    total = price.defaultPrice;
                else
                    total = price.defaultPrice
                            .add(price.defaultPrice
                                    .divide(BigInteger.valueOf(price.rateExtraDays))
                                    .multiply(BigInteger.valueOf(days - price.nDaysDefaultPrice)));

        }
        return total;
    }

    private void startPrices() {
        prices.put(Type.RELEASE, new Price(new BigInteger("40"), Integer.MAX_VALUE, 1));
        prices.put(Type.REGULAR, new Price(new BigInteger("30"), 3, 3));
        prices.put(Type.OLD, new Price(new BigInteger("30"), 5, 5));
    }

    public void startDatabase() {
        filmRepository.save(new Film("12", "Spiderman", Film.Type.RELEASE, false));
    }

    @AllArgsConstructor
    class Price {
        private BigInteger defaultPrice;
        private int nDaysDefaultPrice;
        private int rateExtraDays;
    }

    public Iterable<Film> getAll() {
        return filmRepository.findAll();
    }

    public Film save(Film film){
        return filmRepository.save(film);
    }

}
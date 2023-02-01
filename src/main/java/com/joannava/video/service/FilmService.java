package com.joannava.video.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joannava.video.dao.Film;
import com.joannava.video.dto.RentRequest;
import com.joannava.video.dto.RentedFilm;
import com.joannava.video.repository.FilmRepository;
import com.joannava.video.util.PriceCalculator;

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
        startDatabase();
    }

    @Transactional
    public List<RentedFilm> rent(Iterable<RentRequest> rentRequests) {

        List<RentedFilm> rented = new ArrayList<>();

        for (RentRequest request : rentRequests) {

            Optional<Film> film = filmRepository.findById(request.getId());

            if (!film.isPresent()) {
                rented.add(makeErrorRentedFilm(request.getId(), "Not Such Film In Database"));
            } else if (film.get().isRented()) {
                rented.add(makeErrorRentedFilm(request.getId(), "Already Rented"));
            } else {
                var realFilm = film.get();

                rented.add(RentedFilm.builder()
                        .id(realFilm.getId())
                        .name(realFilm.getName())
                        .price(PriceCalculator.calculate(realFilm.getType(), request.getDays()))
                        .errorOcurred(false)
                        .build());
                realFilm.setRented(true);

                filmRepository.save(realFilm);
            }

        }

        return rented;
    }

    private RentedFilm makeErrorRentedFilm(String id, String errorMessage) {
        return RentedFilm.builder()
                .id(id)
                .errorOcurred(true)
                .errorString(errorMessage)
                .price(new BigInteger("0"))
                .build();
    }

    public void startDatabase() {
        filmRepository.save(new Film("12", "Spiderman", Film.Type.RELEASE, false));
    }

    public Iterable<Film> getAll() {
        return filmRepository.findAll();
    }

    public Film save(Film film) {
        return filmRepository.save(film);
    }

}
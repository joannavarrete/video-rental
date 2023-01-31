package com.joannava.video.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joannava.video.dao.Film;
import com.joannava.video.dao.Film.Type;
import com.joannava.video.dto.RentRequest;
import com.joannava.video.dto.RentedFilm;
import com.joannava.video.repository.FilmRepository;

/**
 * User Story
 * 
 * As a User
 * I wanto to rent films
 * so I can watch them
 * 
 */

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {

    FilmService filmService;

    @Mock
    FilmRepository repository;

    @BeforeEach
    public void beforeEach() {
        filmService = new FilmService(repository);
    }

    @Test
    public void releaseFilmsShouldAlwaysCost40PerDay() {

        Iterable<RentRequest> rentRequests = List.of(
                createRequest(Type.RELEASE, "145", 1),
                createRequest(Type.RELEASE, "155", 10));

        List<RentedFilm> rentedFilm = filmService.rent(rentRequests);

        assertThat(getPriceForId(rentedFilm, "145")).isEqualTo(40d);
        assertThat(getPriceForId(rentedFilm, "155")).isEqualTo(400d);

    }

    @Test
    public void regularFilmShouldCost30TheFirstThreeDays() {

        Iterable<RentRequest> rentRequests = List.of(
                createRequest(Film.Type.REGULAR, "24", 1),
                createRequest(Film.Type.REGULAR, "52", 3));

        List<RentedFilm> rentedFilm = filmService.rent(rentRequests);

        assertThat(getPriceForId(rentedFilm, "24")).isEqualTo(30d);
        assertThat(getPriceForId(rentedFilm, "52")).isEqualTo(30d);
    }

    @Test
    public void regularFilmShouldPayAThirdFromThirdDay() {
        RentRequest request = createRequest(Type.REGULAR, "45", 5);

        List<RentedFilm> rentedFilm = filmService.rent(List.of(request));

        assertThat(getPriceForId(rentedFilm, "45")).isEqualTo(50d);
    }

    @Test
    public void oldFilmShouldCost30TheFirstFiveDays() {

        Iterable<RentRequest> rentRequests = List.of(
                createRequest(Film.Type.OLD, "24", 1),
                createRequest(Film.Type.OLD, "52", 5));

        List<RentedFilm> rentedFilm = filmService.rent(rentRequests);

        assertThat(getPriceForId(rentedFilm, "24")).isEqualTo(30d);
        assertThat(getPriceForId(rentedFilm, "52")).isEqualTo(30d);
    }

    @Test
    public void oldFilmShouldCostAFifthFromFifthDay() {

        RentRequest request = createRequest(Type.OLD, "45", 8);

        List<RentedFilm> rentedFilm = filmService.rent(List.of(request));

        assertThat(getPriceForId(rentedFilm, "45")).isEqualTo(48d);
    }

    @Test
    public void userShouldBeAbleToRentMoreThanOneFilm() {
        Iterable<RentRequest> requests = List.of(
                createRequest(Type.RELEASE, "1", 1),
                createRequest(Type.REGULAR, "2", 1));

        List<RentedFilm> rentedFilms = filmService.rent(requests);

        assertThat(getPriceForId(rentedFilms, "1")).isEqualTo(40d);
        assertThat(getPriceForId(rentedFilms, "2")).isEqualTo(30d);

    }

    @Test
    public void ifRequestedANonExistentFilmShouldReturnError() {
        RentRequest request = new RentRequest("1", 10);
        when(repository.findById("1")).thenReturn(Optional.empty());

        List<RentedFilm> rentedFilms = filmService.rent(List.of(request));

        assertTrue(rentedFilms.get(0).isErrorOcurred());
    }

    @Test
    public void ifRequestedAFilmAlreadyRentedShouldReturnError() {
        Film film = new Film("1", "the beast", Film.Type.REGULAR, true);
        when(repository.findById("1")).thenReturn(Optional.of(film));

        RentRequest request = new RentRequest("1", 10);

        List<RentedFilm> rentedFilms = filmService.rent(List.of(request));

        assertTrue(rentedFilms.get(0).isErrorOcurred());
    }

    private double getPriceForId(List<RentedFilm> rentedFilms, String id) {
        return rentedFilms.stream().filter(f -> f.getId().equals(id)).findFirst().get().getPrice().doubleValue();
    }

    private RentRequest createRequest(Type type, String id, int days) {
        Film film = Film.builder().type(type).id(id).build();

        when(repository.findById(id)).thenReturn(Optional.of(film));

        RentRequest rentRequest = new RentRequest(id, days);
        return rentRequest;
    }

}

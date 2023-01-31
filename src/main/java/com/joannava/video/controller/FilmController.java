package com.joannava.video.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joannava.video.dao.Film;
import com.joannava.video.dto.RentRequest;
import com.joannava.video.dto.RentedFilm;
import com.joannava.video.service.FilmService;

@RestController
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film save(@RequestBody Film film){
        return filmService.save(film);
    }

    @PostMapping("/rent")
    public Iterable<RentedFilm> rent(@RequestBody Iterable<RentRequest> request) {
        return filmService.rent(request);
    }

    // We could/should create a dto an map the dao
    @GetMapping()
    public Iterable<Film> getAll() {
        return filmService.getAll();
    }

}

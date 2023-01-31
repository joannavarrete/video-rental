package com.joannava.video.repository;

import org.springframework.data.repository.CrudRepository;

import com.joannava.video.dao.Film;

public interface FilmRepository extends CrudRepository<Film, String> {
    
}

package com.group4.movie.service;

import com.group4.movie.domain.Movie;
import java.util.List;
public interface MovieService {
    List<Movie> getAllMovies();
    Long saveMovie(Movie movie);
    Movie findMovieById(Long movId);
    void deleteMovieById(Long movId);


}


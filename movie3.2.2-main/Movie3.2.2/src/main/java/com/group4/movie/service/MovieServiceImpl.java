package com.group4.movie.service;

import com.group4.movie.domain.Movie;
import com.group4.movie.entity.MovieEntity;
import com.group4.movie.exception.MovieNotFoundException;
import com.group4.movie.mapper.MovieMapperHelper;
import com.group4.movie.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapperHelper mapperHelper;
    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, MovieMapperHelper mapperHelper) {
        this.movieRepository = movieRepository;
        this.mapperHelper = mapperHelper;
    }
    @Override
    public List<Movie> getAllMovies() {
        List<MovieEntity> movEntities = movieRepository.findAll();
        return mapperHelper.convertMovieEntityListToMovieList(movEntities);
    }

    @Override
    public Long saveMovie(Movie movie) {
        MovieEntity movieEntity = mapperHelper.convertMovieToMovieEntity(movie);
        MovieEntity savedMov =  movieRepository.save(movieEntity);
        return savedMov.getId();
    }

    @Override
    public Movie findMovieById(Long movId) {
        Optional<MovieEntity> byId = movieRepository.findById(movId);
        if(byId.isPresent()){
            MovieEntity foundMov = byId.get();
            return mapperHelper.convertMovieEntityToMovie(foundMov);
        }
        throw new MovieNotFoundException("There is no movie by id " + movId);
    }

    @Override
    public void deleteMovieById(Long movId) {
        movieRepository.deleteById(movId);
    }
}

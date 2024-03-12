package com.group4.movie.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.movie.domain.Movie;
import com.group4.movie.dto.MovieDTO;
import com.group4.movie.entity.MovieEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class MovieMapperHelper {

    private final ObjectMapper mapper;

    @Autowired
    public MovieMapperHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<Movie> convertMovieEntityListToMovieList(List<MovieEntity> movieEntities){
        List<Movie> movies = new ArrayList<>();
        for(MovieEntity acc: movieEntities){
            movies.add(mapper.convertValue(acc, Movie.class));
        }
        return movies;
    }

    public List<MovieDTO> convertMovieListToMovieDTOList(List<Movie> movies){
        List<MovieDTO> movieDTOs = new ArrayList<>();
        for(Movie acc: movies){
            movieDTOs.add(mapper.convertValue(acc, MovieDTO.class));
        }
        return movieDTOs;
    }

    public MovieEntity convertMovieToMovieEntity(Movie movie){
        return mapper.convertValue(movie, MovieEntity.class);
    }

    public Movie convertMovieDTOToMovie(MovieDTO movie){
        return mapper.convertValue(movie, Movie.class);
    }

    public Movie convertMovieEntityToMovie(MovieEntity movieEntity){
        return mapper.convertValue(movieEntity, Movie.class);
    }

    public MovieDTO convertMovieToMovieDTO(Movie movie){
        return mapper.convertValue(movie, MovieDTO.class);
    }
}
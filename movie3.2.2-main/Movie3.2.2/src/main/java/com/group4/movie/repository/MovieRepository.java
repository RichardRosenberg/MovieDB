package com.group4.movie.repository;

import com.group4.movie.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
}

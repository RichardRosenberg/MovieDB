package com.group4.movie.repository;

import com.group4.movie.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findAllByMovieId(Long movieId);
    List<ReviewEntity> findAllByUserId(Long userId);
}
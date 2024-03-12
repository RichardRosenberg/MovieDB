package com.group4.movie.service;

import com.group4.movie.entity.ReviewEntity;
import java.util.List;

public interface ReviewService {
    List<ReviewEntity> getAllReviewsForMovie(Long movieId);
    List<ReviewEntity> getReviewsByUserId(Long userId);
    void saveReview(ReviewEntity review);
    ReviewEntity getReviewById(Long reviewId);
    void updateReview(Long reviewId, String modifiedReviewText);
    void deleteReviewById(Long reviewId);

}

package com.group4.movie.service;

import com.group4.movie.entity.ReviewEntity;
import com.group4.movie.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
    @Override
    public List<ReviewEntity> getAllReviewsForMovie(Long movieId) {
        // Implement logic to get reviews for a specific movie
        return reviewRepository.findAllByMovieId(movieId);
    }
    @Override
    public List<ReviewEntity> getReviewsByUserId(Long userId) {
        return reviewRepository.findAllByUserId(userId);
    }
    @Override
    public void saveReview(ReviewEntity review) {
        reviewRepository.save(review);
    }
    @Override
    public ReviewEntity getReviewById(Long reviewId) {
        Optional<ReviewEntity> optionalReview = reviewRepository.findById(reviewId);
        return optionalReview.orElse(null);
    }
    @Override
    public void updateReview(Long reviewId, String modifiedReviewText) {
        Optional<ReviewEntity> optionalReview = reviewRepository.findById(reviewId);
        optionalReview.ifPresent(review -> {
            review.setReview(modifiedReviewText);
            reviewRepository.save(review);
        });
    }
    @Override
    public void deleteReviewById(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}

package com.group4.movie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_reviews")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    @Column(name = "review")
    private String review;

}

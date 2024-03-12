package com.group4.movie.entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "movie")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name="movie_name")
    private String movieName;

    @Column(name="release_date")
    private String releaseDate;

    @Column(name="genre")
    private String genre;

    @Column(name="director_name")
    private String directorName;

    @Column(name="cast")
    private String cast;

    @Column(name="summary")
    private String summary;

    @Column(name="image")
    private String image;
}




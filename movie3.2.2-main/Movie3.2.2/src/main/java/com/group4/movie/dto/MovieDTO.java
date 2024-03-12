package com.group4.movie.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MovieDTO {
    private long id;
    private String movieName;
    private String releaseDate;
    private String genre;
    private String directorName;
    private String cast;
    private String summary;
    private String image;
}

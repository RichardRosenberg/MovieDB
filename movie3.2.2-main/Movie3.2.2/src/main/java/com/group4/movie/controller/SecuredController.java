package com.group4.movie.controller;

// http://localhost:8080/login (Homepage)

import com.group4.movie.dto.MovieDTO;
import com.group4.movie.entity.MovieEntity;
import com.group4.movie.entity.ReviewEntity;
import com.group4.movie.entity.Role;
import com.group4.movie.exception.MovieNotFoundException;
import com.group4.movie.mapper.MovieMapperHelper;
import com.group4.movie.repository.UserRepository;
import com.group4.movie.service.MovieService;
import com.group4.movie.service.ReviewService;
import jakarta.validation.Valid;
import com.group4.movie.dto.UserDto;
import com.group4.movie.entity.User;
import com.group4.movie.service.UserService;
import com.group4.movie.domain.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/")
public class SecuredController {
    private final MovieService movieService;
    private final MovieMapperHelper movieMapperHelper;
    private final ReviewService reviewService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public SecuredController(UserService userService, MovieService movieService, MovieMapperHelper movieMapperHelper,
                             UserRepository userRepository, ReviewService reviewService) {
        this.userService = userService;
        this.movieService = movieService;
        this.movieMapperHelper = movieMapperHelper;
        this.userRepository = userRepository;
        this.reviewService = reviewService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login/success")
    public String loginUser(@ModelAttribute("user") UserDto userDto, Model model) {
        User existingUser = userService.findUserByUsername(userDto.getUsername());
        if (existingUser == null || !existingUser.getPassword().equals(userDto.getPassword())) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        } else {
            UserDetails userDetails = userService.loadUserByUsername(userDto.getUsername());
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            for (Role role : existingUser.getRoles()) {
                if (role.getName().equals("ROLE_ADMIN")) {
                    return "redirect:/movie-list-crud";
                } else if (role.getName().equals("ROLE_USER")) {
                    return "redirect:/movie-list";
                }
            }
            return "login";
        }
    }

    @GetMapping("/movie-list")
    public String listMovie(Model theMovies) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        List<Movie> allMovies = movieService.getAllMovies();
        List<MovieDTO> movieDTOS = movieMapperHelper.convertMovieListToMovieDTOList(allMovies);
        theMovies.addAttribute("movies", movieDTOS);
        return "movie-list";
    }

    @GetMapping("/movie-list-crud")
    public String listMovieCrud(Model theMovies) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        List<Movie> allMovies = movieService.getAllMovies();
        List<MovieDTO> movieDTOS = movieMapperHelper.convertMovieListToMovieDTOList(allMovies);
        theMovies.addAttribute("movies", movieDTOS);
        return "movie-list-crud";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByUsername(userDto.getUsername());
        if (existingUser != null && existingUser.getUsername() != null && !existingUser.getUsername().isEmpty()) {
            result.rejectValue("username", null,
                    "There is already an account registered with the same username");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }
        userService.saveUser(userDto);
        return "redirect:/login";
    }

    @GetMapping("/showMoviePage")
    public String showMoviePage(@RequestParam("movId") Long movId, Model theModel) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userService.findUserByUsername(userDetails.getUsername());
            if (currentUser == null) {
                return "redirect:/login";
            }
            Movie movie = movieService.findMovieById(movId);
            MovieDTO movieDTO = movieMapperHelper.convertMovieToMovieDTO(movie);
            List<ReviewEntity> reviews = reviewService.getAllReviewsForMovie(movie.getId());
            theModel.addAttribute("reviews", reviews);

            theModel.addAttribute("movie", movieDTO);
            theModel.addAttribute("user", currentUser);
            return "movie-page";
        } catch (MovieNotFoundException exception) {
            theModel.addAttribute("movie", null);
            theModel.addAttribute("user", null);
            theModel.addAttribute("exceptionMessage", exception.getMessage());
            return "movie-page";
        }
    }

    @PostMapping("/upsert")
    public String upsertMovie(@Valid @ModelAttribute("movie") MovieDTO theMovie,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              BindingResult result) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            return "movie-form";
        }
        Movie movie = movieMapperHelper.convertMovieDTOToMovie(theMovie);
        try {
            String fileName = theMovie.getMovieName().replaceAll("\\s+", "") + ".png";
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            movie.setImage(fileName);
        } catch (IOException e) {
            // Handle exception
        }
        movieService.saveMovie(movie);
        return "redirect:/movie-list-crud";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        theModel.addAttribute("movie", new MovieDTO());
        return "movie-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("movId") Long theId, Model theModel) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        try {
            Movie movieById = movieService.findMovieById(theId);
            theModel.addAttribute("movie", movieById);
            return "movie-form";
        } catch (MovieNotFoundException exception) {
            theModel.addAttribute("movie", null);
            theModel.addAttribute("exceptionMessage", exception.getMessage());
            return "movie-form";
        }
    }


    @GetMapping("/delete")
    public String delete(@RequestParam("movId") Long theId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        movieService.deleteMovieById(theId);
        return "redirect:/movie-list";
    }

    @GetMapping("/adminDeleteReview")
    public String adminDeleteReview(@RequestParam("reviewId") Long reviewId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null ) {
            return "redirect:/login";
        }
        reviewService.deleteReviewById(reviewId);
        return "redirect:/movie-list-crud";
    }

    @GetMapping("/review")
    public String showReviewPage() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        return "review";
    }

    @PostMapping("/submitReview")
    public String submitReview(@RequestParam("reviewText") String reviewText,
                               @RequestParam("movId") Long movId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        Movie movie = movieService.findMovieById(movId);
        if (movie == null) {
            return "redirect:/movie-list";
        }
        MovieEntity movieEntity = movieMapperHelper.convertMovieToMovieEntity(movie);
        ReviewEntity review = ReviewEntity.builder()
                .user(currentUser)
                .movie(movieEntity)
                .review(reviewText)
                .build();
        reviewService.saveReview(review);
        return "redirect:/movie-list";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/deleteReview")
    public String deleteReview(@RequestParam("reviewId") Long reviewId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        ReviewEntity review = reviewService.getReviewById(reviewId);
        if (review != null && review.getUser().getId().equals(currentUser.getId())) {
            reviewService.deleteReviewById(reviewId);
        }
        return "redirect:/movie-list";
    }

    @GetMapping("/editReview")
    public String editReviewPage() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }
        return "editReview";
    }

    @PostMapping("/submitEditedReview")
    public String submitEditedReview(@RequestParam("reviewText") String reviewText,
                                     @RequestParam("reviewId") Long reviewId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findUserByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return "redirect:/login";
        }

        ReviewEntity existingReview = reviewService.getReviewById(reviewId);
        if (existingReview == null) {
            return "redirect:/movie-list";
        }
        if (!existingReview.getUser().equals(currentUser)) {
            return "redirect:/movie-list";
        }
        existingReview.setReview(reviewText);
        reviewService.saveReview(existingReview);

        return "redirect:/movie-list";
    }

}



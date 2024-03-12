package com.group4.movie.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    public UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/register/**").permitAll()
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/login/success").permitAll()
                                .requestMatchers("/register/save").permitAll()
                                .requestMatchers("/showMoviePage").permitAll()
                                .requestMatchers("/upsert").permitAll()
                                .requestMatchers("/css/**").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/movie-list").hasRole("USER")
                                .requestMatchers("/review").hasRole("USER")
                                .requestMatchers("/editReview").hasRole("USER")
                                .requestMatchers("/submitReview").hasRole("USER")
                                .requestMatchers("/submitEditedReview").hasRole("USER")
                                .requestMatchers("/editReview").hasRole("USER")
                                .requestMatchers("/updateReview").hasRole("USER")
                                .requestMatchers("/deleteReview").hasRole("USER")
                                .requestMatchers("/showFormForUpdate").hasRole("ADMIN")
                                .requestMatchers("/showFormForAdd").hasRole("ADMIN")
                                .requestMatchers("/upsert").hasRole("ADMIN")
                                .requestMatchers("/delete").hasRole("ADMIN")
                                .requestMatchers("/movie-list-crud").hasRole("ADMIN")
                                .requestMatchers("/adminDeleteReview").hasRole("ADMIN")
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .successHandler((request, response, authentication) -> {
                                    for (GrantedAuthority authority : authentication.getAuthorities()) {
                                        if (authority.getAuthority().equals("ROLE_ADMIN")) {
                                            response.sendRedirect("/movie-list-crud");
                                            return;
                                        } else if (authority.getAuthority().equals("ROLE_USER")) {
                                            response.sendRedirect("/movie-list");
                                            return;
                                        }
                                    }
                                    response.sendRedirect("/login");
                                })
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()

                );
        return http.build();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
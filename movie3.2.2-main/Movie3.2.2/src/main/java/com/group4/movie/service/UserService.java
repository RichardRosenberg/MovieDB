package com.group4.movie.service;

import com.group4.movie.dto.UserDto;
import com.group4.movie.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findUserByUsername(String username);
    List<UserDto> findAllUsers();
    UserDetails loadUserByUsername(String username);
}
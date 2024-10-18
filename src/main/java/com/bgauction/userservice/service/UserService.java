package com.bgauction.userservice.service;

import com.bgauction.userservice.model.dto.UserDto;
import com.bgauction.userservice.model.dto.RegisterUserDto;

import java.util.List;

public interface UserService {
    UserDto findUserById(Long id);
    UserDto saveNewUser(RegisterUserDto userDto);
    void updateUser(UserDto userDto);
    List<UserDto> findAllUsers();
    void deleteUserById(Long id, String email);
}

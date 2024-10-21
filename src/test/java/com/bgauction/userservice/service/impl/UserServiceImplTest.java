package com.bgauction.userservice.service.impl;

import com.bgauction.userservice.exception.NotFoundException;
import com.bgauction.userservice.model.dto.RegisterUserDto;
import com.bgauction.userservice.model.dto.UserDto;
import com.bgauction.userservice.model.entity.Role;
import com.bgauction.userservice.model.entity.User;
import com.bgauction.userservice.repository.UserRepository;
import com.bgauction.userservice.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.bgauction.userservice.util.CreateObjectsForTests.getRegisterUserDto;
import static com.bgauction.userservice.util.CreateObjectsForTests.getUserDtoWithoutId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findUserByIdSuccessfully() {
        RegisterUserDto registerUserDto = getRegisterUserDto();
        UserDto expected = userService.saveNewUser(registerUserDto);
        userRepository.flush();
        UserDto result = userService.findUserById(expected.getId());
        assertThat(expected).isEqualTo(result);
    }

    @Test
    void userNotFoundById() {
        assertThrows(NotFoundException.class, () -> {
            userService.findUserById(10L);
        });
    }

    @Test
    void saveNewUserSuccessfully() {
        RegisterUserDto registerUserDto = getRegisterUserDto();
        String encodedPassword = passwordEncoder.encode(registerUserDto.getPassword());

        UserDto userDto = userService.saveNewUser(registerUserDto);
        assertThat(userDto.getId()).isPositive();
        assertThat(userDto.getEmail()).isEqualTo(registerUserDto.getEmail());
        assertThat(userDto.getUsername()).isEqualTo(registerUserDto.getUsername());
        userRepository.flush();
        User user = userRepository.findById(userDto.getId()).get();
        assertThat(passwordEncoder.matches(registerUserDto.getPassword(), encodedPassword)).isTrue();
        assertThat(user.getEnabled()).isEqualTo(true);
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getCreated()).isNotNull().isInThePast();
        assertThat(user.getUpdated()).isNotNull().isInThePast();
    }

    @Test
    void saveNewUserWithInvalidData() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder().email("a").username("a").password("a").build();
        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveNewUser(registerUserDto);
        });
    }

    @Test
    void updateUserSuccessfully() throws InterruptedException {
        RegisterUserDto registerUserDto = getRegisterUserDto();
        UserDto savedDto = userService.saveNewUser(registerUserDto);
        userRepository.flush();
        Long id = savedDto.getId();
        User savedUser = userRepository.findById(id).get();
        LocalDateTime updateTime = savedUser.getUpdated();

        UserDto forUpdateDto = getUserDtoWithoutId();
        forUpdateDto.setId(id);
        forUpdateDto.setCity("Riga");

        userService.updateUser(forUpdateDto);
        userRepository.flush();

        User updatedUser = userRepository.findById(id).get();
        assertThat(updatedUser.getEnabled()).isEqualTo(true);
        assertThat(updatedUser.getRole()).isEqualTo(Role.USER);
        assertThat(updatedUser.getCity()).isEqualTo(forUpdateDto.getCity());
        assertThat(updatedUser.getUpdated()).isNotNull().isInThePast();
        assertThat(updatedUser.getUpdated()).isAfter(updateTime);
    }

    @Test
    void findAllUsersSuccessfully() {
        RegisterUserDto registerUserDto = getRegisterUserDto();
        UserDto saved = userService.saveNewUser(registerUserDto);
        userRepository.flush();
        List<UserDto> list = userService.findAllUsers();
        assertThat(list.size()).isEqualTo(4);
    }

    @Test
    void deleteUserByIdSuccessfully() {
        RegisterUserDto registerUserDto = getRegisterUserDto();
        UserDto saved = userService.saveNewUser(registerUserDto);
        userRepository.flush();
        userService.deleteUserById(saved.getId(), saved.getEmail());
        userRepository.flush();
        List<UserDto> list = userService.findAllUsers();
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    void deleteUserByIdWhenEmailIsInvalid() {
        RegisterUserDto registerUserDto = getRegisterUserDto();
        UserDto saved = userService.saveNewUser(registerUserDto);
        userRepository.flush();

        assertThrows(AccessDeniedException.class, () -> {
            userService.deleteUserById(saved.getId(), "hre@mail.com");
        });
    }
}

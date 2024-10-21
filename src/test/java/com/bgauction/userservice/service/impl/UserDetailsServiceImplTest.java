package com.bgauction.userservice.service.impl;

import com.bgauction.userservice.exception.NotFoundException;
import com.bgauction.userservice.model.dto.RegisterUserDto;
import com.bgauction.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.bgauction.userservice.util.CreateObjectsForTests.getRegisterUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserDetailsServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void loadUserByUsernameSuccessfully() {
        RegisterUserDto registerUserDto = getRegisterUserDto();
        userService.saveNewUser(registerUserDto);
        userRepository.flush();
        UserDetails userDetails = userDetailsService.loadUserByUsername(registerUserDto.getEmail());
        assertThat(userDetails.getUsername()).isEqualTo(registerUserDto.getEmail());
        assertThat(userDetails.getAuthorities().stream().findFirst().get().toString())
                .isEqualTo("USER");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(passwordEncoder.matches(registerUserDto.getPassword(), userDetails.getPassword())).isTrue();
    }

    @Test
    void loadUserByUsernameUnsuccessfully() {
        assertThrows(NotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("");
        });
    }
}

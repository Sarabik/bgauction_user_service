package com.bgauction.userservice.controller;

import com.bgauction.userservice.model.dto.UserDto;
import com.bgauction.userservice.security.JwtUtil;
import com.bgauction.userservice.security.SpringSecurityConfig;
import com.bgauction.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;

import static com.bgauction.userservice.util.CreateObjectsForTests.getListOfUserDto;
import static com.bgauction.userservice.util.CreateObjectsForTests.getUserDto;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
@Import({SpringSecurityConfig.class, JwtUtil.class})
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    UserDetailsService userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    public String jwtToken;

    @BeforeEach
    public void setUp() {
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        "1@email.com",
                        "password1",
                        Collections.singletonList(new SimpleGrantedAuthority("USER"))
                ));

        jwtToken = jwtUtil.generateToken("1@email.com");
    }

    @Test
    void getAllUserList() throws Exception {

        when(userService.findAllUsers()).thenReturn(getListOfUserDto());

        mockMvc.perform(
                get("/user")
                        .header("Authorization", "Bearer " + jwtToken)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getUserByIdSuccessfully() throws Exception {

        UserDto userDto = getUserDto(1);

        when(userService.findUserById(userDto.getId())).thenReturn(userDto);

        mockMvc.perform(
                        get("/user/{id}", userDto.getId())
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUserByIdWhenAskingForOtherUserData() throws Exception {

        UserDto userDto = getUserDto(2);

        when(userService.findUserById(userDto.getId())).thenReturn(userDto);

        mockMvc.perform(
                        get("/user/{id}", userDto.getId())
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserByIdWithInvalidId() throws Exception {

        mockMvc.perform(
                        get("/user/{id}", 0L)
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserSuccessfully() throws Exception {

        UserDto userDto = getUserDto(1);

        mockMvc.perform(
                        put("/user/{id}", userDto.getId())
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Location"));

        verify(userService, times(1)).updateUser(userDto);
    }

    @Test
    void updateUserWithInvalidPathVariable() throws Exception {

        UserDto userDto = getUserDto(2);

        mockMvc.perform(
                        put("/user/{id}", 1L)
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithInvalidId() throws Exception {

        UserDto userDto = getUserDto(1);

        mockMvc.perform(
                        put("/user/{id}", 0L)
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUserSuccessfully() throws Exception {
        UserDto userDto = getUserDto(1);

        mockMvc.perform(
                        delete("/user/{id}", userDto.getId())
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(userDto.getId(), userDto.getEmail());
    }

    @Test
    void deleteUserWithInvalidId() throws Exception {

        mockMvc.perform(
                        delete("/user/{id}", 0L)
                                .header("Authorization", "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}

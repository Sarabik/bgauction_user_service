package com.bgauction.userservice.controller;

import com.bgauction.userservice.exceptionHandler.ErrorsResponse;
import com.bgauction.userservice.model.dto.LoginUserDto;
import com.bgauction.userservice.model.dto.RegisterUserDto;
import com.bgauction.userservice.model.dto.UserDto;
import com.bgauction.userservice.security.JwtUtil;
import com.bgauction.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@Tag(name = "User API", description = "Operations related to Users")
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Operation(summary = "Register new user", description = "Returns saved new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User is created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorsResponse.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Access is forbidden", content = @Content())
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterUserDto userDto) {
        UserDto savedUserDto = userService.saveNewUser(userDto);
        return new ResponseEntity<>(savedUserDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Login user", description = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully login"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Access is forbidden", content = @Content())
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateToken(userDto.getEmail());
        Map<String, String> token = new HashMap<>();
        token.put("token", jwt);
        log.info("Successfully login User with email {}", userDto.getEmail());
        return ResponseEntity.ok(token);
    }

}

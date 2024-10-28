package com.bgauction.userservice.controller;

import com.bgauction.userservice.exception.InvalidIdException;
import com.bgauction.userservice.exceptionHandler.ErrorsResponse;
import com.bgauction.userservice.model.dto.UserDto;
import com.bgauction.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;

@Log4j2
@RestController
@Tag(name = "User API", description = "Operations related to Users")
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user's list", description = "Returns a list of all users (empty list is available)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list is successfully found"),
            @ApiResponse(responseCode = "401", description = "User is unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Access is forbidden", content = @Content())
    })
    @GetMapping
    public List<UserDto> getAllUserList() {
        return userService.findAllUsers();
    }

    @Operation(summary = "Get user by ID", description = "Returns a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is successfully found by id",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsResponse.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Access is forbidden", content = @Content()),
            @ApiResponse(responseCode = "404", description = "User is not found",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsResponse.class)))
    })
    @GetMapping("{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Principal principal) {
        if (id <= 0) {
            throw new InvalidIdException("Id must be greater than 0");
        }
        log.info("Principal: {}", principal.getName());
        UserDto userDto = userService.findUserById(id);
        if (!principal.getName().equals(userDto.getEmail())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @Operation(summary = "Update user by ID", description = "Returns URI with location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User is updated successfully",
                    headers = {@Header(name = "Location", description = "URI of the updated user")}),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsResponse.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Access is forbidden", content = @Content()),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsResponse.class)))
    })
    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UserDto userDto,
                                        Principal principal) {
        if (id <= 0) {
            throw new InvalidIdException("Path variable id must be greater than 0");
        }
        if (!id.equals(userDto.getId())) {
            throw new InvalidIdException("Path variable id is not equal to User id");
        }
        if (!principal.getName().equals(userDto.getEmail())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.updateUser(userDto);
        String locationUrl = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .buildAndExpand(id)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", locationUrl);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete user by id", description = "Deletes user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User is deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsResponse.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Access is forbidden", content = @Content()),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorsResponse.class)))
    })
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {
        if (id <= 0) {
            throw new InvalidIdException("Path variable id must be greater than 0");
        }
        String email = principal.getName();
        userService.deleteUserById(id, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

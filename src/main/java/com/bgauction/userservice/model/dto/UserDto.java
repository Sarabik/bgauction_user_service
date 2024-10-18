package com.bgauction.userservice.model.dto;

import com.bgauction.userservice.model.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 3, max = 25)
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Role role;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String firstName;

    private String lastName;

    private String country;

    private String city;

    private String deliveryInfo;
}

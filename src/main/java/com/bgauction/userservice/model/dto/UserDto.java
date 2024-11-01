package com.bgauction.userservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 3, max = 25)
    private String username;

    @NotBlank
    @Email
    private String email;

    private String firstName;

    private String lastName;

    private String country;

    private String city;

    private String deliveryInfo;
}

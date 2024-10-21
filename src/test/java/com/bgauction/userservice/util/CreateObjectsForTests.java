package com.bgauction.userservice.util;

import com.bgauction.userservice.model.dto.RegisterUserDto;
import com.bgauction.userservice.model.dto.UserDto;

import java.util.List;

public class CreateObjectsForTests {

    public static RegisterUserDto getRegisterUserDto() {
        return RegisterUserDto.builder()
                        .email("1@email.com")
                        .username("user1")
                        .password("password1")
                        .build();
    }

    public static UserDto getUserDtoWithoutId() {
        return UserDto.builder()
                .email("1@email.com")
                .username("user1")
                .build();
    }

    public static UserDto getUserDto(int i) {
        return UserDto.builder()
                .id((long) i)
                .username("username" + i)
                .email(i + "@email.com")
                .firstName("firstname" + i)
                .lastName("lastname" + i)
                .country("country" + i)
                .city("city" + i)
                .deliveryInfo("deliveryinfo" + i)
                .build();
    }

    public static List<UserDto> getListOfUserDto() {
        return List.of(
                getUserDto(1),
                getUserDto(2),
                getUserDto(3)
        );
    }
}

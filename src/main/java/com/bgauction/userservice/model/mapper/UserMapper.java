package com.bgauction.userservice.model.mapper;

import com.bgauction.userservice.model.dto.UserDto;
import com.bgauction.userservice.model.dto.RegisterUserDto;
import com.bgauction.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User userDtoToUser(UserDto dto);
    UserDto userToUserDto(User user);
    User UserSavindDtoToUser(RegisterUserDto dto);
}

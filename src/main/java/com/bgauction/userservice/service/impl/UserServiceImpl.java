package com.bgauction.userservice.service.impl;

import com.bgauction.userservice.exception.NotFoundException;
import com.bgauction.userservice.model.dto.RegisterUserDto;
import com.bgauction.userservice.model.dto.UserDto;
import com.bgauction.userservice.model.entity.Role;
import com.bgauction.userservice.model.entity.User;
import com.bgauction.userservice.model.mapper.UserMapper;
import com.bgauction.userservice.repository.UserRepository;
import com.bgauction.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto findUserById(Long id) {
        log.info("Trying to find User with id: {}", id);
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.info("Found User with id {}: {}", id, user);
            return userMapper.userToUserDto(user);
        } else {
            log.warn("User with id {} is not found", id);
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public UserDto saveNewUser(RegisterUserDto userDto) {
        User userForSave = userMapper.UserSavindDtoToUser(userDto);
        userForSave.setEnabled(true);
        userForSave.setRole(Role.USER);
        userForSave.setPassword(passwordEncoder.encode(userForSave.getPassword()));
        log.info("Saving User: {}", userForSave);

        User savedUser = userRepository.save(userForSave);
        log.info("User saved: {}", savedUser);

        return userMapper.userToUserDto(savedUser);
    }

    @Override
    public void updateUser(UserDto userDto) {
        checkIfExistsById(userDto.getId());
        User userForUpdate = userMapper.userDtoToUser(userDto);
        log.info("Updating User: {}", userForUpdate);
        userRepository.save(userForUpdate);
        log.info("User with id {} updated", userForUpdate);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> list = userRepository.findAll();
        log.info("Get all User's list. List size: {}", list.size());
        return list.stream().map(userMapper::userToUserDto).toList();
    }

    @Override
    public void deleteUserById(Long id, String email) {
        checkIfExistsById(id);
        log.info("Deleting User with id: {}, email: {}", id, email);
        int deleted = userRepository.deleteByIdAndEmail(id, email);
        if (deleted == 0) {
            throw new AccessDeniedException("");
        }
        log.info("User with id {} is deleted", id);
    }

    private void checkIfExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("User with id {} is not found", id);
            throw new NotFoundException("User with id " + id + " not found");
        }
    }
}

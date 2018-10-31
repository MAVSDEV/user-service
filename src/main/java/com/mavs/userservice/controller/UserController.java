package com.mavs.userservice.controller;

import com.google.common.base.Preconditions;
import com.mavs.userservice.controller.dto.ResponseUserDto;
import com.mavs.userservice.exception.ResourceNotFoundException;
import com.mavs.userservice.model.User;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Value("${app.users.key:default}")
    private String mySecret;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<ResponseUserDto> findAll() {
        log.warn("-----mySecret: {}", mySecret);
        return userService.findAll().stream().map(this::transformUserModelToDto).collect(Collectors.toList());
    }

    @GetMapping("/id/{id}")
    public ResponseUserDto findById(@PathVariable("id") Integer id) {
        return transformUserOptionalModelToDto(userService.findById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseUserDto findByName(@PathVariable("username") String username) {
        return transformUserOptionalModelToDto(userService.findByName(username));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody User user) {
        Preconditions.checkNotNull(user);
        userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Integer id) {
        userService.delete(id);
    }

    private ResponseUserDto transformUserModelToDto(User user) {
        return ResponseUserDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .build();
    }

    private ResponseUserDto transformUserOptionalModelToDto(Optional<User> userOptional) {
        if (userOptional.isPresent()) {
            return transformUserModelToDto(userOptional.get());
        }
        throw new ResourceNotFoundException();
    }
}


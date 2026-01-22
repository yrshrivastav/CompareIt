package com.compareit.app.controller;


import com.compareit.app.dto.ApiResponse;
import com.compareit.app.dto.UserDto;
import com.compareit.app.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(
                new ApiResponse("User created successfully"),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
//        return ResponseEntity.ok(
//                new ApiResponse(true, "Users fetched successfully")
//        );
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
//        return ResponseEntity.ok(
//                new ApiResponse(true, "User found")
//        );
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                new ApiResponse("User deleted successfully with ID: " + id)
        );
    }
}



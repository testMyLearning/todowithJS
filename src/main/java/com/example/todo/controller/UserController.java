package com.example.todo.controller;

import com.example.todo.model.dto.user.CreateUserRequest;
import com.example.todo.model.dto.user.UpdateUserRequest;
import com.example.todo.model.dto.user.UserResponse;
import com.example.todo.model.entity.User;
import com.example.todo.security.jwt.JwtService;
import com.example.todo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // ПУБЛИЧНЫЕ ЭНДПОИНТЫ
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userService.findByEmail(email);
        String token = jwtService.generateJwtToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        String token = jwtService.generateJwtToken(user);

        Map<String, Object> result = new HashMap<>();
        result.put("user", user.getEmail());
        result.put("token", token);
        result.put("type", "Bearer");
        result.put("email", user.getEmail());
        result.put("name", user.getName());
        result.put("role", user.getRole());
        return ResponseEntity.status(201).body(result);
    }

    // ЗАЩИЩЁННЫЕ ЭНДПОИНТЫ
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PatchMapping("/users/{email}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String email,
                                                   @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(email, request));
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
        boolean status = userService.deleteUser(email);
        return status ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        UserResponse response = userService.getProfile(userDetails.getEmail());
        return ResponseEntity.ok(response);
    }
}
package com.FRM.User;

import com.FRM.Exception.DuplicateEmailException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final  UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest user){
        Optional<User> existingUser = userRepository.findUserByEmail(user.email());
        if (userRepository.findUserByEmail(user.email()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }
        User user1 = new User();
        user1.setUserName(user.username());
        user1.setEmail(user.email());
        user1.setPassword(user.password());
        user1.setRoles(Set.of(Roles.VIEWER));
        userRepository.save(user1);
         return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "userId", user1.getUserId(),
                        "message", "User created successfully"
                ));
    }
}

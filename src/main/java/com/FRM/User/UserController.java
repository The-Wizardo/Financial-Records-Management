package com.FRM.User;

import com.FRM.Exception.ApiResponse;
import com.FRM.Exception.ApiResponseUtil;
import com.FRM.Exception.DuplicateEmailException;
import com.FRM.Exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody UserRequest user) {
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
        Map<String, Object> data = Map.of(
                "userId", user1.getUserId(),
                "userName", user1.getUserName(),
                "email", user1.getEmail(),
                "roles", user1.getRoles()
        );


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "User Created successfully", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        userRepository.delete(user);

        return ResponseEntity.ok(ApiResponseUtil.success("User Deleted successfully"));
    }
}

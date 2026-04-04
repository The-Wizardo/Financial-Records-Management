package com.FRM.Auth;

import com.FRM.Config.JwtService;
import com.FRM.Exception.ApiResponse;
import com.FRM.Exception.DuplicateEmailException;
import com.FRM.User.Roles;
import com.FRM.User.User;
import com.FRM.User.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody UserRequest user) {
        if (userRepository.findUserByEmailAndIsActiveTrue(user.email()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }
        User user1 = new User();
        user1.setUserName(user.username());
        user1.setEmail(user.email());
        user1.setPassword(passwordEncoder.encode(user.password()));
        if (user.roles() != null && !user.roles().isEmpty()) {
            user1.setRoles(user.roles());
        } else {
            user1.setRoles(Set.of(Roles.VIEWER));
        }
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        assert userDetails != null;
        User user = userDetails.getUser();

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getUserId(),
                user.getRoles()
        );

        return ResponseEntity.ok(Map.of("token", token));
    }
}

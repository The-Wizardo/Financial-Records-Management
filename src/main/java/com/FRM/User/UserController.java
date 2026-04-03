package com.FRM.User;

import com.FRM.Exception.ApiResponse;
import com.FRM.Exception.ApiResponseUtil;
import com.FRM.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id) {

        User user = userRepository.findByUserIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponseUtil.success("User Deleted successfully"));
    }
}

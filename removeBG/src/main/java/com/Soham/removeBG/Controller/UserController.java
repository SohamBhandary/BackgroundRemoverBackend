package com.Soham.removeBG.Controller;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Response.RemoveBgResponse;
import com.Soham.removeBG.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public RemoveBgResponse createOrUpdateUser(@RequestBody UserDTO userDTO, Authentication authentication) {
        try {
            // ✅ Check if authentication is not null before using it
            if (authentication != null && !authentication.getName().equals(userDTO.getClerkId())) {
                return RemoveBgResponse.builder()
                        .success(false)
                        .data("Unauthorized")
                        .statusCode(HttpStatus.FORBIDDEN)
                        .build();
            }

            // ✅ Proceed to save user
            UserDTO user = userService.saveUser(userDTO);

            return RemoveBgResponse.builder()
                    .success(true)
                    .data(user)
                    .statusCode(HttpStatus.CREATED)
                    .build();

        } catch (Exception e) {
            // ✅ Log the error for debugging
            e.printStackTrace(); // Optional: log this properly in real apps
            return RemoveBgResponse.builder()
                    .success(false)
                    .data(e.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}

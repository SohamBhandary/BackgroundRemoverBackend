package com.Soham.removeBG.Controller;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Response.RemoveBgResponse;
import com.Soham.removeBG.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

            // ✅ Ensure clerkId is never null
            if (userDTO.getClerkId() == null || userDTO.getClerkId().isEmpty()) {
                userDTO.setClerkId(authentication.getName()); // ✅ This line ensures no null clerkId
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
    @GetMapping("/credits")
    public ResponseEntity<?> getUserCredits(Authentication authentication){
        RemoveBgResponse bgResponse=null;
        try{
            if(authentication.getName().isEmpty()|| authentication.getName()==null){
                bgResponse=RemoveBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .data("User does not have permission/acces")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(bgResponse);
            }
            String clerkId=authentication.getName();
            UserDTO exsistingUser=userService.getUserByClerkId(clerkId);
            Map<String,Integer> map= new HashMap<>();
            map.put("credits",exsistingUser.getCredits());
            bgResponse=RemoveBgResponse.builder()
                    .statusCode(HttpStatus.OK)
                    .data(map)
                    .success(true)
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bgResponse);

        } catch (Exception e) {bgResponse=RemoveBgResponse.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .data("Somethign wnet wrong")
                .success(false)
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(bgResponse);

        }
    }
}


package com.Soham.removeBG.Controller;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Response.RemoveBgResponse;
import com.Soham.removeBG.Service.RemoveBackgroundService;
import com.Soham.removeBG.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final RemoveBackgroundService removeBackgroundService;
    private final UserService userService;

    @PostMapping("/remove-background")
    public ResponseEntity<?> removeBAckground(@RequestParam("file") MultipartFile file,
                                              Authentication authentication) {
        RemoveBgResponse response = null;
        Map<String, Object> responseMap = new HashMap<>();

        try {
            if (authentication.getName() == null || authentication.getName().isEmpty()) {
                response = RemoveBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .success(false)
                        .data("User does not have permission/access")
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            UserDTO userDTO = userService.getUserByClerkId(authentication.getName());

            if (userDTO.getCredits() == 0) {
                responseMap.put("message", "No credit balance");
                responseMap.put("creditBalance", userDTO.getCredits());
                response = RemoveBgResponse.builder()
                        .success(false)
                        .data(responseMap)
                        .statusCode(HttpStatus.BAD_REQUEST)
                        .build();
                return ResponseEntity.ok(response);
            }

            // 🖼️ Process the image
            byte[] imageBytes = removeBackgroundService.removeBackground(file);

            // 🧮 Deduct 1 credit
            userDTO.setCredits(userDTO.getCredits() - 1);
            userService.saveUser(userDTO);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(base64Image);

        } catch (Exception e) {
            response = RemoveBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .success(false)
                    .data("Something went wrong")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

package com.Soham.removeBG.Controller;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Response.RemoveBgResponse;
import com.Soham.removeBG.Service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class ClerkWebHookController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    private final UserService userService;

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixId,
                                                @RequestHeader("svix-timestamp") String svixTimestamp,
                                                @RequestHeader("svix-signature") String svixSignature,
                                                @RequestBody String payload) {
        RemoveBgResponse response = null;

        try {
            boolean isValid = verifyWebhookSignature(svixId, svixTimestamp, svixSignature, payload);
            if (!isValid) {
                response = RemoveBgResponse.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED)
                        .data("Invalid webhook signature")
                        .success(false)
                        .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.path("type").asText();



            switch (eventType) {
                case "user.created":
                    handleUserCreated(rootNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdated(rootNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDeleted(rootNode.path("data"));
                    break;
                default:
                    System.out.println("⚠ Unhandled event type: " + eventType);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();  // Log full stack trace for debugging
            response = RemoveBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data("Something went wrong")
                    .success(false)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asText();



        try {
            userService.deleteUserByClerkId(clerkId);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void handleUserUpdated(JsonNode data) {
        String clerkId = data.path("id").asText();

        System.out.println("🔄 Updating user: " + clerkId);

        try {
            UserDTO existingUser = userService.getUserByClerkId(clerkId);
            existingUser.setEmail(data.path("email_addresses").path(0).path("email_address").asText());
            existingUser.setFirstName(data.path("first_name").asText());
            existingUser.setLastName(data.path("last_name").asText());
            existingUser.setPhotoUrl(data.path("image_url").asText());
            userService.saveUser(existingUser);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void handleUserCreated(JsonNode data) {
        String clerkId = data.path("id").asText();
        System.out.println("🆕 Creating user: " + clerkId);

        UserDTO newUser = UserDTO.builder()
                .clerkId(clerkId)
                .email(data.path("email_addresses").path(0).path("email_address").asText())
                .firstName(data.path("first_name").asText())
                .lastName(data.path("last_name").asText())
                .build();

        try {
            userService.saveUser(newUser);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // Dummy method for now — implement Clerk signature verification later
    private boolean verifyWebhookSignature(String svixId, String svixTimestamp, String svixSignature, String payload) {
        return true;
    }
}

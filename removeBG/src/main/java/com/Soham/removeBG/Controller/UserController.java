package com.Soham.removeBG.Controller;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Response.RemoveBGResponse;
import com.Soham.removeBG.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    public RemoveBGResponse createOrUpdateUser(@RequestBody UserDTO userDTO){
        try{
            UserDTO user=  userService.saveUser(userDTO);
            return RemoveBGResponse.builder()
                    .success(true)
                    .data(user)
                    .statusCode(HttpStatus.CREATED)
                    .build();

        } catch (Exception e) {

            return RemoveBGResponse.builder()
                    .success(false)
                    .data(e.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

        }



    }
}

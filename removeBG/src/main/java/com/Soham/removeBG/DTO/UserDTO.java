package com.Soham.removeBG.DTO;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserDTO {
    private String clerkID;

    private String email;
    private String firstName;
    private String lastName;


    private String photoUrl;
    private Integer credits;
}

package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Entity.UserEnitity;
import com.Soham.removeBG.Repository.UserRepository;
import com.Soham.removeBG.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImple implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        Optional<UserEnitity> optionalUser = userRepository.findBYClerkID(userDTO.getClerkID());

        UserEnitity exsisitingUser;
        if (optionalUser.isPresent()) {
            exsisitingUser = optionalUser.get();
            exsisitingUser.setEmail(userDTO.getEmail());
            exsisitingUser.setFirstName(userDTO.getFirstName());
            exsisitingUser.setLastName(userDTO.getLastName());
            exsisitingUser.setPhotoUrl(userDTO.getPhotoUrl());
            if (userDTO.getCredits() != null) {
                exsisitingUser.setCredits(userDTO.getCredits());
            }
            exsisitingUser=  userRepository.save(exsisitingUser);
          return   mapToDTO(exsisitingUser);


        }
       UserEnitity newUSer= mapTOEntity(userDTO);
        userRepository.save(newUSer);
        return mapToDTO(newUSer);
    }

    private UserDTO mapToDTO(UserEnitity newUSer) {
       return UserDTO.builder()
                .clerkID(newUSer.getClerkID())
                .credits(newUSer.getCredits())
                .email(newUSer.getEmail())
                .firstName(newUSer.getFirstName())
                .lastName(newUSer.getLastName())
                .build();

    }

    private UserEnitity mapTOEntity(UserDTO userDTO) {
        return   UserEnitity.builder()
                .clerkID(userDTO.getClerkID())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName((userDTO.getLastName()))
                .photoUrl(userDTO.getPhotoUrl())
                .build();
    }
}

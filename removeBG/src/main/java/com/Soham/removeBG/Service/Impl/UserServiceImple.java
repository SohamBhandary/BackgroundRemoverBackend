package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.DTO.UserDTO;

import com.Soham.removeBG.Entity.UserEntity;
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
        Optional<UserEntity> optionalUser = userRepository.findByClerkId(userDTO.getClerkID());

        UserEntity exsisitingUser;
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
       UserEntity newUSer= mapTOEntity(userDTO);
        userRepository.save(newUSer);
        return mapToDTO(newUSer);
    }

    private UserDTO mapToDTO(UserEntity newUSer) {
       return UserDTO.builder()
                .clerkID(newUSer.getClerkId())
                .credits(newUSer.getCredits())
                .email(newUSer.getEmail())
                .firstName(newUSer.getFirstName())
                .lastName(newUSer.getLastName())
                .build();

    }

    private UserEntity mapTOEntity(UserDTO userDTO) {
        return   UserEntity.builder()
                .clerkId(userDTO.getClerkID())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName((userDTO.getLastName()))
                .photoUrl(userDTO.getPhotoUrl())
                .build();
    }
}

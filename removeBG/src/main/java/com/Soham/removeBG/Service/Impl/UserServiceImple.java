package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Entity.UserEntity;
import com.Soham.removeBG.Repository.UserRepository;
import com.Soham.removeBG.Service.UserService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Builder
@Service
@RequiredArgsConstructor
public class UserServiceImple implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserDTO saveUser(UserDTO userDTO){
      Optional<UserEntity> optionalUser= userRepository.findByClerkId(userDTO.getClerkId());
      if(optionalUser.isPresent()){
          UserEntity exsistingUser= optionalUser.get();
          exsistingUser.setEmail(userDTO.getEmail());
          exsistingUser.setFirstName(userDTO.getFirstName());
          exsistingUser.setLastName((userDTO.getLastName()));
          exsistingUser.setPhotoUrl(userDTO.getPhotoUrl());
          if(userDTO.getCredits()!=null){
              exsistingUser.setCredits((userDTO.getCredits()));
          }
         exsistingUser= userRepository.save(exsistingUser);
         return  mapToDTO(exsistingUser);



      }
        UserEntity newUser= mapToEntity(userDTO);
        userRepository.save(newUser);
        return mapToDTO(newUser);



    }

    private UserDTO mapToDTO(UserEntity newUser) {
      return   UserDTO.builder()
                .clerkId(newUser.getClerkId())
              .credits(newUser.getCredits())
              .email(newUser.getEmail())
              .firstName(newUser.getFirstName())
              .lastName(newUser.getLastName())
              .build();

    }

    private UserEntity mapToEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .clerkId((userDTO.getClerkId()))
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .photoUrl(userDTO.getPhotoUrl())
                .build();

    }


}

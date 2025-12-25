package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Entity.UserEntity;
import com.Soham.removeBG.Repository.UserRepository;
import com.Soham.removeBG.Service.UserService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Builder
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImple implements UserService {
    private final UserRepository userRepository;

    @Override
    public void deleteUserByClerkId(String clerkId) {

        log.info("Attempting to delete user with clerkId={}", clerkId);

        Optional<UserEntity> optionalUser = userRepository.findByClerkId(clerkId);

        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            log.info("User found in DB. email={}", userEntity.getEmail());
            userRepository.delete(userEntity);
            log.info("User deleted successfully for clerkId={}", clerkId);
        } else {
            log.warn("No user found for clerkId={}", clerkId);
        }
    }

    @Override
    public UserDTO getUserByClerkId(String clerkId){
        log.info("Fetching user by clerkId={}", clerkId);
      UserEntity userEntity=  userRepository.findByClerkId(clerkId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
      log.info("User fetched successfully for clerkId={}", clerkId);
     return mapToDTO(userEntity);

    }

    @Override
    public UserDTO saveUser(UserDTO userDTO){
        log.info("Saving user with clerkId={}", userDTO.getClerkId());
      Optional<UserEntity> optionalUser= userRepository.findByClerkId(userDTO.getClerkId());
      if(optionalUser.isPresent()){
          log.info("Existing user found. Updating details. clerkId={}",
                  userDTO.getClerkId());
          UserEntity exsistingUser= optionalUser.get();
          exsistingUser.setEmail(userDTO.getEmail());
          exsistingUser.setFirstName(userDTO.getFirstName());
          exsistingUser.setLastName((userDTO.getLastName()));
          exsistingUser.setPhotoUrl(String.valueOf(userDTO.getPhotoUrl()));
          if(userDTO.getCredits()!=null){
              log.info("Updating credits for clerkId={}, credits={}",
                      userDTO.getClerkId(), userDTO.getCredits());
              exsistingUser.setCredits((userDTO.getCredits()));
          }
         exsistingUser= userRepository.save(exsistingUser);
          log.info("User updated successfully for clerkId={}",
                  userDTO.getClerkId());
          return  mapToDTO(exsistingUser);
      }
        UserEntity newUser= mapToEntity(userDTO);
        log.info("No existing user found. Creating new user. clerkId={}",
                userDTO.getClerkId());
        userRepository.save(newUser);
        log.info("New user created successfully for clerkId={}",
                userDTO.getClerkId());
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
                .photoUrl(String.valueOf(userDTO.getPhotoUrl()))
                .build();

    }


}

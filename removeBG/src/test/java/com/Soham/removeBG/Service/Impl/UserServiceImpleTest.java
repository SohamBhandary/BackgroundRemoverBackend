package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Entity.UserEntity;
import com.Soham.removeBG.Repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpleTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImple userServiceImple;

    @DisplayName("TestForGetUserByClerkId")
    @Test
    void getUserByClerkId_shouldReturnUserDTO_whenUserExists() {
        UserEntity userEntity = new UserEntity();
        userEntity.setClerkId("clerk123");
        userEntity.setEmail("test@example.com");
        userEntity.setCredits(10);
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        when(userRepository.findByClerkId("clerk123"))
                .thenReturn(Optional.of(userEntity));
        UserDTO result = userServiceImple.getUserByClerkId("clerk123");
        assertNotNull(result);
        assertEquals("clerk123", result.getClerkId());
        assertEquals(10, result.getCredits());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByClerkId("clerk123");
    }

    @Test
    public void TestSaveUser(){
        UserDTO userDTO = new UserDTO();
        userDTO.setClerkId("newClerk");
        userDTO.setEmail("new@example.com");
        userDTO.setFirstName("New");
        userDTO.setLastName("User");
        userDTO.setCredits(5);
        when(userRepository.findByClerkId("newClerk"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        UserDTO savedUser = userServiceImple.saveUser(userDTO);
        assertNotNull(savedUser);
        assertEquals("newClerk", savedUser.getClerkId());
        assertEquals("new@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }


}
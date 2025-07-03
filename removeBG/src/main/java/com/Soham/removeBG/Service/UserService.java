package com.Soham.removeBG.Service;

import com.Soham.removeBG.DTO.UserDTO;

public interface UserService {
    UserDTO saveUser(UserDTO userDTO);
   UserDTO getUserByClerkId(String clerkId);
   void deleteUserByClerkId(String clerkId);


}

package com.Soham.removeBG.Repository;

import com.Soham.removeBG.Entity.UserEnitity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <UserEnitity,Long>{
    Optional<UserEnitity> findBYClerkID(String clerkId);
    Boolean exsistsByClerkId(String clerkId);


}

package com.Soham.removeBG.Repository;

import com.Soham.removeBG.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByClerkId(String clerkId);
    Boolean exsistsByClerkId(String clerkId);


}

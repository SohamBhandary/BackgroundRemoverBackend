package com.Soham.removeBG.Repository;

import com.Soham.removeBG.Entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    Optional<OrderEntity> findByOrderId(String orderId);
}

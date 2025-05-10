package com.farmterra.backend.repository;

import com.farmterra.backend.model.Order;
import com.farmterra.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(Order.OrderStatus status);
}

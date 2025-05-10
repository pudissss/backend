package com.farmterra.backend.service;

import com.farmterra.backend.model.Order;
import com.farmterra.backend.model.Product;
import com.farmterra.backend.model.User;
import com.farmterra.backend.repository.OrderRepository;
import com.farmterra.backend.repository.ProductRepository;
import com.farmterra.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrder(Order order) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User buyer = userRepository.findByEmail(currentUsername)
            .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        // Set buyer for the order
        order.setBuyer(buyer);
        
        // Calculate total amount and validate stock
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Order.OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
            
            // Validate stock
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            // Update product stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            
            // Set item price and calculate total
            item.setPrice(product.getPrice());
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);
        
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByBuyer(User buyer) {
        return orderRepository.findByBuyer(buyer);
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}

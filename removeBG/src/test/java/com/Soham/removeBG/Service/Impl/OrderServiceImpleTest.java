package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.Entity.OrderEntity;
import com.Soham.removeBG.Repository.OrderRepository;
import com.Soham.removeBG.Service.RazorPayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpleTest {
    @Mock
    private RazorPayService razorPayService;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderServiceImple orderService;
    @Test
    void createOrder_shouldCreateOrderSuccessfully() throws RazorpayException {
        Order razorpayOrder = mock(Order.class);
        when(razorpayOrder.get("id")).thenReturn("order_123");
        when(razorPayService.createOrder(anyDouble(), anyString()))
                .thenReturn(razorpayOrder);
        Order result = orderService.createOrder("Basic", "clerk123");
        assertNotNull(result);
        verify(orderRepository).save(any(OrderEntity.class));
    }

}
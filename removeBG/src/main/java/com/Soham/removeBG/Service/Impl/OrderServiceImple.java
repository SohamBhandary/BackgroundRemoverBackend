package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.Entity.OrderEntity;
import com.Soham.removeBG.Repository.OrderRepository;
import com.Soham.removeBG.Service.OrderService;
import com.Soham.removeBG.Service.RazorPayService;
import com.Soham.removeBG.Service.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImple implements OrderService {
    private final RazorPayService razorPayService;
    private final OrderRepository orderRepository;
    private static final Map<String,PlanDetails> PLAN_DETAILS= Map.of(
            "Basic",new PlanDetails("Basic",100,499.00),
            "Premium",new PlanDetails("Basic",250,899.00),
            "Ultimate",new PlanDetails("Basic",1000,1499.00));
    private record PlanDetails(String name,int credits,double amount){

    }
    @Override
    public Order createOrder(String planId, String clerkId) throws RazorpayException {
        log.info("Creating order for clerkid={} with planId={}",clerkId,planId);
        PlanDetails details= PLAN_DETAILS.get(planId);
        if(details==null){
            log.warn("Invalid planId recevied:{}",planId);
            throw new IllegalArgumentException("Invalid planId:"+planId);

        }
        try{
            Order razorpayOrder=razorPayService.createOrder(details.amount(),"INR");
            log.info("Razorpay order created successfully. orderId={}",
                    (Object) razorpayOrder.get("id"));
            OrderEntity newOrder=OrderEntity.builder()
                    .clerkId(clerkId)
                    .plan(details.name())
                    .credits(details.credits())
                    .amount(details.amount())
                    .orderId(razorpayOrder.get("id"))
                    .build();
            orderRepository.save(newOrder);
            return razorpayOrder;
        }catch (RazorpayException e ){
            log.error("Failed to create Razorpay order for clerkId={}", clerkId, e);
            throw  new RazorpayException("Error while creating the order",e);

        }

    }
}

package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.DTO.UserDTO;
import com.Soham.removeBG.Entity.OrderEntity;
import com.Soham.removeBG.Repository.OrderRepository;
import com.Soham.removeBG.Service.RazorPayService;
import com.Soham.removeBG.Service.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RazorPayServiceImpl implements RazorPayService {
    @org.springframework.beans.factory.annotation.Value("${razor_key_id}")
    private String razorpayKeyId;
    @Value("${razor_secret_key}")
    private String razorpayKeySecret;
   private final OrderRepository orderRepository;
   private final UserService userService;


    @Override
    public Order createOrder(Double amount, String currency) throws RazorpayException {
        log.info("Creating RazorPay Order. amount={},currency={}",amount,currency);
        try{
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId,razorpayKeySecret);
            JSONObject orderRequest= new JSONObject();
            orderRequest.put("amount",amount*100);
            orderRequest.put("currency",currency);
            orderRequest.put("receipt","order _rcptid_"+System.currentTimeMillis());
            log.info("Calling Razorpay to create order");
            return razorpayClient.orders.create(orderRequest);

        }catch (RazorpayException e) {
            e.printStackTrace();
            log.error("Error while creating Razorpay order", e);
            throw new RazorpayException("Razorpay error"+e.getMessage());
        }
    }

    @Override
    public Map<String, Object> verifyPayment(String razorpayOrderId) throws RazorpayException {
        log.info("Verifying payment for razorpayOrderId={}", razorpayOrderId);

        Map<String,Object> returnvalue= new HashMap<>();
     try{
         RazorpayClient razorpayClient= new RazorpayClient(razorpayKeyId,razorpayKeySecret);
         log.info("Fetching Razorpay order details for orderId={}", razorpayOrderId);
         Order orderInfo=razorpayClient.orders.fetch(razorpayOrderId);
         log.info("Razorpay order status={} for orderId={}",
                 orderInfo.get("status"), razorpayOrderId);
         if(orderInfo.get("status").toString().equalsIgnoreCase("paid")){
            OrderEntity exsisitngOrder= orderRepository.findByOrderId(razorpayOrderId)
                    .orElseThrow(()->new RuntimeException("Order not found:"+razorpayOrderId));
            if(exsisitngOrder.getPayment()){
                returnvalue.put("success",false);
                returnvalue.put("message","payment failed");
                return returnvalue;
            }
           UserDTO userDTO= userService.getUserByClerkId(exsisitngOrder.getClerkId());
            userDTO.setCredits((userDTO.getCredits() + exsisitngOrder.getCredits()));

            userService.saveUser(userDTO);
            exsisitngOrder.setPayment(true);
            orderRepository.save(exsisitngOrder);
             log.info("Payment verified and credits added. clerkId={}, creditsAdded={}",
                     exsisitngOrder.getClerkId(), exsisitngOrder.getCredits());
             returnvalue.put("success",true);
            returnvalue.put("message","Credits Added");
            return  returnvalue;

         }

     } catch (Exception e) {
         log.error("Error while verifying payment for orderId={}", razorpayOrderId, e);
         throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error while verifying there payment");
     }
     return returnvalue;


    }
}

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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RazorPayServiceImpl implements RazorPayService {
    @org.springframework.beans.factory.annotation.Value("${razor_key_id}")
    private String razorpayKeyId;
    @Value("${razor_secret_key}")
    private String razorpayKeySecret;
   private final OrderRepository orderRepository;
   private final UserService userService;


    @Override
    public Order createOrder(Double amount, String currency) throws RazorpayException {
        try{
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId,razorpayKeySecret);
            JSONObject orderRequest= new JSONObject();
            orderRequest.put("amount",amount*100);
            orderRequest.put("currency",currency);
            orderRequest.put("receipt","order _rcptid_"+System.currentTimeMillis());
            return razorpayClient.orders.create(orderRequest);


        }catch (RazorpayException e) {
            e.printStackTrace();
            throw new RazorpayException("Razorpay error"+e.getMessage());
        }
    }

    @Override
    public Map<String, Object> verifyPayment(String razorpayOrderId) throws RazorpayException {
     Map<String,Object> returnvalue= new HashMap<>();
     try{
         RazorpayClient razorpayClient= new RazorpayClient(razorpayKeyId,razorpayKeySecret);
         Order orderInfo=razorpayClient.orders.fetch(razorpayOrderId);
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
             returnvalue.put("success",true);
            returnvalue.put("message","Credits Added");
            return  returnvalue;

         }

     } catch (Exception e) {
         throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error while verifying there payment");
     }
     return returnvalue;


    }
}

package com.Soham.removeBG.Service.Impl;

import com.Soham.removeBG.Service.RazorPayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorPayServiceImpl implements RazorPayService {
    @org.springframework.beans.factory.annotation.Value("${razor_key_id}")
    private String razorpayKeyId;
    @Value("${razor_secret_key}")
    private String razorpayKeySecret;


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
}

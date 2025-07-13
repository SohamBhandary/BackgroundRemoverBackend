package com.Soham.removeBG.Controller;

import com.Soham.removeBG.DTO.RazorPayOrderDTO;
import com.Soham.removeBG.Response.RemoveBgResponse;
import com.Soham.removeBG.Service.OrderService;
import com.Soham.removeBG.Service.RazorPayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private  final RazorPayService razorPayService;
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam String planId, Authentication authentication) throws RazorpayException{
        Map<String,Object> responseMap= new HashMap<>();
        RemoveBgResponse response=null;
        if (authentication.getName() == null || authentication.getName().isEmpty()) {
            response = RemoveBgResponse.builder()
                    .statusCode(HttpStatus.FORBIDDEN)
                    .success(false)
                    .data("User does not have permission/access")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try{
         Order order= orderService.createOrder(planId,authentication.getName());
        RazorPayOrderDTO  responseDTO= convertToDTO(order);
        RemoveBgResponse.builder()
                .success(true)
                .data(responseDTO)
                .statusCode(HttpStatus.CREATED)
                .build();
        return ResponseEntity.ok(response);
        } catch (Exception e) {
            response=RemoveBgResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(e.getMessage())
                    .success(false)
                    .build();
            return ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR)).body(response);


        }
    }

    private RazorPayOrderDTO convertToDTO(Order order) {
       return RazorPayOrderDTO.builder()
                .id(order.get("id"))
                .entity(order.get("entity"))
                .amount(order.get("amount"))
                .currency(order.get("currency"))
                .status(order.get("status"))
                .created_at(order.get("created_at"))
                .receipt(order.get("receipt"))
                .build();



    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOrder(@RequestBody Map<String ,Object> req) throws RazorpayException{

        try{
            String razorpayOrderId=req.get("razorpay_order_id").toString();
            Map<String,Object> returnValue= razorPayService.verifyPayment(razorpayOrderId);
            return  ResponseEntity.ok(returnValue);

        } catch (RazorpayException e) {
            Map<String,Object> errorResponse= new HashMap<>();
            errorResponse.put("succes",false);
            errorResponse.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);


        }

    }
}

package com.Soham.removeBG.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface RazorPayService {
    Order createOrder(Double amount, String currency) throws RazorpayException;
}

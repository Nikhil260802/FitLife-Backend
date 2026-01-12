package com.FitLife.Service;

import com.FitLife.Dto.PaymentDetailsDto;
import com.FitLife.Entity.PaymentDetails;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PaymentDetailsService {
    PaymentDetailsDto getByPaymentId(int paymentId);

    List<PaymentDetailsDto> getAllPaymentDetails();

    ResponseEntity<?> createOrder(String email, int courseId);

    ResponseEntity<?> verifyPayment(String razorpayOrderId,
                                    String razorpayPaymentId,
                                    String razorpaySignature,
                                    int userId,
                                    int courseId);
}

package com.FitLife.Controller;

import com.FitLife.Dto.PaymentDetailsDto;
import com.FitLife.Service.PaymentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentDetailsController {

    @Autowired
    private PaymentDetailsService paymentDetailsService;

    @PostMapping("/public/createOrder")
    public ResponseEntity<?> createOrder(@RequestParam("courseId") int courseId, @RequestParam("email") String email) {
        return paymentDetailsService.createOrder(email, courseId);
    }

    @PostMapping("/public/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestParam String razorpayOrderId,
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpaySignature,
            @RequestParam int userId,
            @RequestParam int courseId)
    {
        return paymentDetailsService.verifyPayment(
                razorpayOrderId, razorpayPaymentId, razorpaySignature, userId, courseId);
    }

    @GetMapping("/public/getByPaymentId/{paymentId}")
    public PaymentDetailsDto getByPaymentId(@PathVariable int paymentId) {
        return paymentDetailsService.getByPaymentId(paymentId);
    }

    @GetMapping("/admin/getAllPaymentDetails")
    public List<PaymentDetailsDto> getAllPaymentDetails() {
        return paymentDetailsService.getAllPaymentDetails();
    }
}

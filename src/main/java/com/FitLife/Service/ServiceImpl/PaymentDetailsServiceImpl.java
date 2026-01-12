package com.FitLife.Service.ServiceImpl;

import com.FitLife.Dto.PaymentDetailsDto;
import com.FitLife.Entity.Course;
import com.FitLife.Entity.EnrollmentDetails;
import com.FitLife.Entity.PaymentDetails;
import com.FitLife.Entity.User;
import com.FitLife.Repository.CourseRepository;
import com.FitLife.Repository.EnrollmentDetailsRepository;
import com.FitLife.Repository.PaymentDetailsRepository;
import com.FitLife.Repository.UserRepository;
import com.FitLife.Service.PaymentDetailsService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService {
    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Autowired
    private PaymentDetailsRepository paymentRepo;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EnrollmentDetailsRepository enrollmentRepo;

    @Override
    public PaymentDetailsDto getByPaymentId(int paymentId) {
        PaymentDetails paymentDetails = paymentRepo.findById(paymentId).orElse(null);
        return modelMapper.map(paymentDetails, PaymentDetailsDto.class);
    }

    @Override
    public List<PaymentDetailsDto> getAllPaymentDetails() {
        List<PaymentDetails> paymentDetails = paymentRepo.findAll();
        return paymentDetails.stream().map(paymentDetails1 -> this.modelMapper.map(paymentDetails1, PaymentDetailsDto.class)).toList();
    }

    @Override
    public ResponseEntity<?> createOrder(String email, int courseId) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            RazorpayClient razorpay = new RazorpayClient(
                    razorpayKey,
                    razorpaySecret
            );

            JSONObject options = new JSONObject();
            options.put("amount", course.getPrice() * 100);
            options.put("currency", "INR");
            options.put("receipt", "rcpt_" + System.currentTimeMillis());

            Order order = razorpay.orders.create(options);

            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("courseId", courseId);
            response.put("email", email);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating Razorpay Order: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> verifyPayment(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature,
            int userId,
            int courseId) {

        try {
            boolean isValid = verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

            if (!isValid) {
                ResponseEntity.badRequest()
                        .body(Map.of("status", "FAILED", "message", "Invalid signature"));
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            if (enrollmentRepo.existsByUserAndCourse(user, course)) {
                return ResponseEntity.ok(
                        Map.of("status", "SUCCESS", "message", "Already enrolled")
                );
            }

            PaymentDetails payment = new PaymentDetails();
            payment.setTransactionId(razorpayOrderId);
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setRazorpaySignature(razorpaySignature);
            payment.setAmount(course.getPrice());
            payment.setPaymentStatus("SUCCESS");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUser(user);
            payment.setCourse(course);
            paymentRepo.save(payment);

            EnrollmentDetails enrollment = new EnrollmentDetails();
            enrollment.setUser(user);
            enrollment.setCourse(course);
            enrollment.setTransactionId(razorpayOrderId);
            enrollment.setPaymentId(razorpayPaymentId);
            enrollment.setEnrollmentDate(LocalDate.now());
            enrollment.setValidTill(LocalDate.now().plusMonths(3));
            enrollment.setStatus(EnrollmentDetails.Status.ACTIVE);
            enrollmentRepo.save(enrollment);

            emailService.sendMail(user.getEmail(),
                    "Payment Successful",
                    "Your payment was successful and you are enrolled in " + course.getCourseName());

            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "message", "Payment successful and enrollment completed"
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "FAILED", "message", e.getMessage()));
        }
    }

    private boolean verifySignature(
            String orderId,
            String paymentId,
            String razorpaySignature) {

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", razorpaySignature);

            String secret = "tLN54VVr9NHqZthfRSQfAYLJ";

            return Utils.verifyPaymentSignature(options, secret);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
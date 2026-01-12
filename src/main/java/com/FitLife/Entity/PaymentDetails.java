package com.FitLife.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_details")
public class PaymentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private String razorpayPaymentId;

    @Column(nullable = false)
    private String razorpaySignature;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private String paymentStatus;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnoreProperties("payments")
    private User user;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    @JsonIgnoreProperties("payments")
    private Course course;
}

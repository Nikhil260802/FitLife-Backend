package com.FitLife.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentDetailsDto {
    private int amount;
    private String transactionId;
    private LocalDate paymentDate;
    private String paymentStatus;
    private String courseName;
}

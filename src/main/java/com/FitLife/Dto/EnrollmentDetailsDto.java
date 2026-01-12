package com.FitLife.Dto;

import com.FitLife.Entity.Course;
import com.FitLife.Entity.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnrollmentDetailsDto {
    private String status;
    private LocalDate enrollmentDate;
    private String timeSlot;
    private User user;
    private Course course;
}

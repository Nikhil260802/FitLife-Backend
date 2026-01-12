package com.FitLife.Service;

import com.FitLife.Dto.EnrollmentDetailsDto;
import com.FitLife.Entity.EnrollmentDetails;
import com.FitLife.Entity.PaymentDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EnrollmentDetailsService {
    EnrollmentDetailsDto getById(int enrollId);

    List<EnrollmentDetailsDto> getAllEnrollmentDetails();

    Boolean addEnrollmentDetails(EnrollmentDetails enrollmentDetails, int userId, int CourseId);

    Boolean updateEnrollmentDetails(int enrollId, EnrollmentDetails enrollmentDetails);

    Boolean deleteEnrollmentDetails(int enrollId);

    EnrollmentDetails createEnrollmentAfterSuccessfulPayment(PaymentDetails payment);

    EnrollmentDetails getEnrollmentIfActive(Integer userId, Integer courseId);

}
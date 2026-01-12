package com.FitLife.Repository;

import com.FitLife.Entity.Course;
import com.FitLife.Entity.EnrollmentDetails;
import com.FitLife.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentDetailsRepository extends JpaRepository<EnrollmentDetails, Integer> {
    Optional<EnrollmentDetails> findByUserUserIdAndCourseCourseId(Integer userId, Integer courseId);
    List<EnrollmentDetails> findByUserUserId(Integer userId);
    boolean existsByUserAndCourse(User user, Course course);
}

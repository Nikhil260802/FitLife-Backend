package com.FitLife.Service.ServiceImpl;

import com.FitLife.Dto.EnrollmentDetailsDto;
import com.FitLife.Entity.Course;
import com.FitLife.Entity.EnrollmentDetails;
import com.FitLife.Entity.PaymentDetails;
import com.FitLife.Entity.User;
import com.FitLife.Repository.CourseRepository;
import com.FitLife.Repository.EnrollmentDetailsRepository;
import com.FitLife.Repository.UserRepository;
import com.FitLife.Service.EnrollmentDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentDetailsServiceImpl implements EnrollmentDetailsService {

    @Autowired
    private EnrollmentDetailsRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public EnrollmentDetailsDto getById(int enrollId) {
        EnrollmentDetails enrollment = repository.findById(enrollId).orElse(null);
        return enrollment == null ? null : modelMapper.map(enrollment, EnrollmentDetailsDto.class);
    }

    @Override
    public List<EnrollmentDetailsDto> getAllEnrollmentDetails() {
        List<EnrollmentDetails> enrollment = repository.findAll();
        return enrollment.stream()
                .map(enroll -> modelMapper.map(enroll, EnrollmentDetailsDto.class))
                .toList();
    }

    @Override
    @Transactional
    public Boolean addEnrollmentDetails(EnrollmentDetails enrollmentDetails, int userId, int courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Optional<EnrollmentDetails> existing = repository.findByUserUserIdAndCourseCourseId(userId, courseId);
        if (existing.isPresent()) {
            return false;
        }

        enrollmentDetails.setUser(user);
        enrollmentDetails.setCourse(course);
        enrollmentDetails.setEnrollmentDate(LocalDate.now());
        enrollmentDetails.setStatus(EnrollmentDetails.Status.PENDING);
        repository.save(enrollmentDetails);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateEnrollmentDetails(int enrollId, EnrollmentDetails enrollmentDetails) {
        EnrollmentDetails exist = repository.findById(enrollId).orElse(null);
        if (exist == null) return false;

        if (enrollmentDetails.getStatus() != null) exist.setStatus(enrollmentDetails.getStatus());
        if (enrollmentDetails.getValidTill() != null) exist.setValidTill(enrollmentDetails.getValidTill());
        repository.save(exist);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteEnrollmentDetails(int enrollId) {
        if (!repository.existsById(enrollId)) return false;
        repository.deleteById(enrollId);
        return true;
    }

    @Transactional
    @Override
    public EnrollmentDetails createEnrollmentAfterSuccessfulPayment(PaymentDetails payment) {
        if (payment == null) throw new IllegalArgumentException("Payment is null");

        User user = payment.getUser();
        Course course = payment.getCourse();

        Integer userId = user.getUserId();
        Integer courseId = course.getCourseId();

        Optional<EnrollmentDetails> opt = repository.findByUserUserIdAndCourseCourseId(userId, courseId);

        return opt.map(existing -> {
            existing.setStatus(EnrollmentDetails.Status.ACTIVE);
            existing.setEnrollmentDate(LocalDate.now());
            existing.setValidTill(LocalDate.now().plusMonths(Long.parseLong(course.getDuration())));
            existing.setPaymentId(payment.getRazorpayPaymentId());
            existing.setTransactionId(payment.getTransactionId());
            return repository.save(existing);
        }).orElseGet(() ->   {
            EnrollmentDetails e = new EnrollmentDetails();
            e.setUser(user);
            e.setCourse(course);
            e.setStatus(EnrollmentDetails.Status.ACTIVE);
            e.setEnrollmentDate(LocalDate.now());
            e.setValidTill(LocalDate.now().plusMonths(Long.parseLong(course.getDuration())));
            e.setPaymentId(payment.getRazorpayPaymentId());
            e.setTransactionId(payment.getTransactionId());
            return repository.save(e);
        });
    }

    @Override
    public EnrollmentDetails getEnrollmentIfActive(Integer userId, Integer courseId) {
        return repository.findByUserUserIdAndCourseCourseId(userId, courseId)
                .filter(e -> e.getStatus() == EnrollmentDetails.Status.ACTIVE)
                .orElse(null);
    }

    @Scheduled(cron = "0 0 6,19 * * *")
    private void sendReminder() {
        List<EnrollmentDetails> enrollments = repository.findAll();
        LocalTime now = LocalTime.now();

        for (EnrollmentDetails enrollment : enrollments) {
            try {
                String ts = "9";
                LocalTime timeSlot = LocalTime.parse(ts);

                if (timeSlot.equals(now) || timeSlot.equals(now.plusHours(1))) {
                    User user = enrollment.getUser();
                    if (user == null) continue;
                    String email = user.getEmail();
                    if (email == null) continue;

                    String subject = "Reminder: Your Scheduled FitLife Session";
                    String body = String.format("Hello %s,\nYour session is scheduled at %s. Please join on time.",
                            user.getUserName(), timeSlot.toString());
                    emailService.sendMail(email, subject, body);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

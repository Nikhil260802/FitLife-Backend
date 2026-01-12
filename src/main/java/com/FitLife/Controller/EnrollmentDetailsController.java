package com.FitLife.Controller;

import com.FitLife.Dto.EnrollmentDetailsDto;
import com.FitLife.Entity.EnrollmentDetails;
import com.FitLife.Service.EnrollmentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class EnrollmentDetailsController {
    @Autowired
    private EnrollmentDetailsService enrollmentDetailsService;

    @GetMapping("/public/getById/{enrollId}")
    public ResponseEntity<?> getById(@PathVariable int enrollId) {
        EnrollmentDetailsDto dto = enrollmentDetailsService.getById(enrollId);
        if (dto == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enrollment not found");
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin/getAllEnrollmentDetails")
    public ResponseEntity<?> getAllEnrollmentDetails() {
        List<EnrollmentDetailsDto> enrollmentDetailsDtos = enrollmentDetailsService.getAllEnrollmentDetails();
        if (enrollmentDetailsDtos.isEmpty())
            return new ResponseEntity<>("There are no enrollments", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(enrollmentDetailsDtos, HttpStatus.OK);
    }

    @PostMapping("/public/addEnrollmentDetails/{userId}/{courseId}")
    public ResponseEntity<?> addEnrollmentDetails(@RequestBody EnrollmentDetails enrollmentDetails,
                                                  @PathVariable int userId,
                                                  @PathVariable int courseId) {
        boolean flag = enrollmentDetailsService.addEnrollmentDetails(enrollmentDetails, userId, courseId);
        if (flag) return ResponseEntity.ok("Successfully Enrolled (PENDING)");

        return ResponseEntity.status(HttpStatus.CONFLICT).body("Enrollment Failed, Try again");
    }

    @PostMapping("/admin/updateEnrollmentDetails/{enrollId}")
    public ResponseEntity<?> updateEnrollmentDetails(@PathVariable int enrollId, @RequestBody EnrollmentDetails enrollmentDetails) {
        Boolean updated = enrollmentDetailsService.updateEnrollmentDetails(enrollId, enrollmentDetails);
        if (updated) return ResponseEntity.ok("Enrollment updated");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update failed");
    }

    @DeleteMapping("/admin/deleteEnrollmentDetails/{enrollId}")
    public ResponseEntity<?> deleteEnrollmentDetails(@PathVariable int enrollId) {
        Boolean deleted = enrollmentDetailsService.deleteEnrollmentDetails(enrollId);
        if (deleted) return ResponseEntity.ok("Enrollment deleted");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete failed");
    }

    @GetMapping("/public/checkAccess")
    public ResponseEntity<?> checkAccess(@RequestParam("userId") int userId, @RequestParam("courseId") int courseId) {
        EnrollmentDetails enrollment = enrollmentDetailsService.getEnrollmentIfActive(userId, courseId);
        if (enrollment != null) return ResponseEntity.ok("ACCESS_GRANTED");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ACCESS_DENIED");
    }
}

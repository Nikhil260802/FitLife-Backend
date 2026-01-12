package com.FitLife.Controller;

import com.FitLife.Dto.CourseDto;
import com.FitLife.Entity.Course;
import com.FitLife.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/")
@Validated
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/public/getByCourseId/{courseId}")
    public ResponseEntity<?> getByCourseId(@PathVariable int courseId) {
        CourseDto course = courseService.getByCourseId(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no Course exists");
        }
        return ResponseEntity.ok(course);
    }

    @GetMapping("/public/getAllCourses")
    public ResponseEntity<?> getAllCourses() {
        List<CourseDto> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no courses exists");
        }
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/admin/addCourse")
    public ResponseEntity<?> addCourse(@RequestParam("image") MultipartFile image, @RequestParam("course") String course) {
        boolean flag = courseService.addCourse(image, course);
        if (flag) return ResponseEntity.ok("You successfully added");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Try again later");
    }

    @PutMapping("/admin/updateCourse/{courseId}")
    public ResponseEntity<?> updateCourse(@PathVariable int courseId, @RequestBody Course course) {
        boolean flag = courseService.updateCourse(courseId, course);
        if (flag) return ResponseEntity.ok("You successfully updated");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Try again later");
    }

    @DeleteMapping("/admin/deleteCourse/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable int courseId) {
        boolean flag = courseService.deleteCourse(courseId);
        if (flag) return ResponseEntity.ok("Course successfully deleted");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Try again later");
    }

    @PostMapping("/admin/sendSessionLink")
    public ResponseEntity<String> sendSessionLink(
            @RequestParam("email") String email,
            @RequestParam("date") LocalDate meetingDate,
            @RequestParam("time") LocalTime meetingTime,
            @RequestParam("link") String meetingLink) {

        String response = courseService.sendSessionLink(email, meetingDate, meetingTime, meetingLink);
        return ResponseEntity.ok(response);
    }
}
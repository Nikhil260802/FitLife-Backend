package com.FitLife.Service;

import com.FitLife.Dto.CourseDto;
import com.FitLife.Entity.Course;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CourseService {

    public CourseDto getByCourseId(int courseId);

    public List<CourseDto> getAllCourses();

    public Boolean addCourse(MultipartFile image, String course);

    public Boolean updateCourse(int courseId, Course course);

    public Boolean deleteCourse(int courseId);

    public String sendSessionLink(String email, LocalDate meetingDate, LocalTime meetingTime, String meetingLink);
}
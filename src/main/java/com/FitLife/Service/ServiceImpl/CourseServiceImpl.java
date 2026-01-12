package com.FitLife.Service.ServiceImpl;

import com.FitLife.Dto.CourseDto;
import com.FitLife.Dto.VideoDto;
import com.FitLife.Entity.Course;
import com.FitLife.Entity.User;
import com.FitLife.Repository.CourseRepository;
import com.FitLife.Repository.UserRepository;
import com.FitLife.Service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    @Value("${fitlife.upload.image-dir}")
    private String uploadDir;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public CourseDto getByCourseId(int courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return null;

        CourseDto dto = modelMapper.map(course, CourseDto.class);

        List<VideoDto> videoDtos = course.getVideos().stream()
                .map(v -> modelMapper.map(v, VideoDto.class))
                .toList();
        dto.setVideos(videoDtos);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(course -> {
            CourseDto dto = modelMapper.map(course, CourseDto.class);
            List<VideoDto> videoDtos = course.getVideos().stream()
                    .map(video -> modelMapper.map(video, VideoDto.class))
                    .toList();
            dto.setVideos(videoDtos);
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public Boolean addCourse(MultipartFile image, String courseJson) {
        try {
            Course newCourse = objectMapper.readValue(courseJson, Course.class);

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String original = Path.of(Objects.requireNonNull(image.getOriginalFilename())).getFileName().toString();
            String ext = "";
            int i = original.lastIndexOf('.');
            if (i > 0) ext = original.substring(i);
            String filename = UUID.randomUUID().toString() + ext;

            Path target = uploadPath.resolve(filename);
            Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            newCourse.setImagePath(uploadDir + filename);

            courseRepository.save(newCourse);
            return true;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store image file", ex);
        }
    }

    @Override
    @Transactional
    public Boolean updateCourse(int courseId, Course course) {
        Course exist = courseRepository.findById(courseId).orElse(null);
        if (exist == null) return false;

        exist.setCourseName(course.getCourseName());
        exist.setPrice(course.getPrice());
        exist.setDescription(course.getDescription());
        if (course.getImagePath() != null && !course.getImagePath().isBlank()) {
            exist.setImagePath(course.getImagePath());
        }
        exist.setDuration(course.getDuration());
        exist.setCategory(course.getCategory());

        courseRepository.save(exist);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteCourse(int courseId) {
        if (!courseRepository.existsById(courseId)) return false;
        courseRepository.deleteById(courseId);
        return true;
    }

    @Override
    public String sendSessionLink(String email, LocalDate meetingDate, LocalTime meetingTime, String meetingLink) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            emailService.sendSessionLink(email, user.getUserName(), meetingDate.toString(), meetingTime.toString(), meetingLink);
            return "Session invitation sent to " + email;
        } else {
            return "User with email " + email + " not found!";
        }
    }

}

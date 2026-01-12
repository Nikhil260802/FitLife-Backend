package com.FitLife.Service.ServiceImpl;

import com.FitLife.Entity.Course;
import com.FitLife.Entity.EnrollmentDetails;
import com.FitLife.Entity.Video;
import com.FitLife.Repository.CourseRepository;
import com.FitLife.Repository.VideoRepository;
import com.FitLife.Service.EnrollmentDetailsService;
import com.FitLife.Service.VideoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

//    public final String uploadDir = "D:\\Project\\Personal Project\\FitLife\\src\\main\\resources\\static\\Videos";
//    public final String Upload_Dir = new ClassPathResource("static/image/").getFile().getAbsolutePath();

    @Value("${file.upload-videos-dir}")
    private String uploadDir;

    @Value("${file.video-url-prefix}")
    private String videoUrlPrefix;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EnrollmentDetailsService enrollmentService;

    private Path getUploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public Resource accessVideo(String fileName, Integer userId, Integer courseId) throws Exception {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Filename is required");
        }
        if (userId == null || courseId == null) {
            throw new IllegalArgumentException("userId and courseId are required to check access");
        }

        EnrollmentDetails enrollment = enrollmentService.getEnrollmentIfActive(userId, courseId);
        if (enrollment == null) {
            throw new SecurityException("User is not enrolled or enrollment is not active");
        }

        Path uploadPath = getUploadPath();
        Path filePath = uploadPath.resolve(fileName).normalize();

        if (!filePath.startsWith(uploadPath)) {
            throw new SecurityException("Invalid file path");
        }

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("Video file not found: " + fileName);
        }

        return resource;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCourseVideosUrls(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<Video> videos = videoRepository.findByCourseCourseId(courseId);

        List<String> urls = new ArrayList<>();
        for (Video v : videos) {
            String fileName = Paths.get(v.getVideoPath()).getFileName().toString();
            urls.add(videoUrlPrefix + fileName + "?courseId=" + courseId);
        }
        return urls;
    }

    @Override
    @Transactional
    public boolean addVideo(MultipartFile[] files, Integer courseId) throws Exception {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("At least one video file is required");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Path uploadPath = getUploadPath();
        Files.createDirectories(uploadPath);

        List<Video> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            if (original.contains("..")) {
                throw new SecurityException("Filename contains invalid path sequence " + original);
            }

            String lower = original.toLowerCase();
            if (!(lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".ogg"))) {
                throw new IllegalArgumentException("Unsupported video format: " + original);
            }

            String ext = "";
            int i = original.lastIndexOf('.');
            if (i >= 0) ext = original.substring(i);

            String filename = UUID.randomUUID().toString() + ext;
            Path target = uploadPath.resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            Video video = new Video();
            video.setVideoName(original);

            // store a relative path so DB doesn't contain absolute system paths
            video.setVideoPath("/uploads/videos/" + filename);
            video.setCourse(course);
            saved.add(video);
        }

        videoRepository.saveAll(saved);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteVideo(Integer videoId) throws Exception {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        String vp = video.getVideoPath();
        String fileName = Paths.get(vp).getFileName().toString();

        Path uploadPath = getUploadPath();
        Path filePath = uploadPath.resolve(fileName).normalize();

        if (!filePath.startsWith(uploadPath)) {
            throw new SecurityException("Invalid file path");
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (Exception ex) {
            return false;
        }

        videoRepository.deleteById(videoId);
        return true;
    }
}
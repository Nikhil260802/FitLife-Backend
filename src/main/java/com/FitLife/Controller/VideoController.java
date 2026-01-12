package com.FitLife.Controller;

import com.FitLife.Service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/accessVideo/{fileName}")
    public ResponseEntity<Resource> accessVideo(@PathVariable String fileName,
                                                @RequestParam Integer userId,
                                                @RequestParam Integer courseId) throws Exception {
        Resource resource = videoService.accessVideo(fileName, userId, courseId);

        String contentType = null;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (Exception ex) {
            // ignore and fallback
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/accessVideo/list/{courseId}")
    public ResponseEntity<List<String>> getCourseVideos(@PathVariable Integer courseId) {
        List<String> urls = videoService.getCourseVideosUrls(courseId);
        return ResponseEntity.ok(urls);
    }

    @PostMapping("/admin/addVideo")
    public ResponseEntity<?> addVideo(@RequestParam("videos") MultipartFile[] videos,
                                      @RequestParam("courseId") Integer courseId) {
        try {
            boolean ok = videoService.addVideo(videos, courseId);
            if (ok) return ResponseEntity.ok("Videos uploaded successfully");
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/deleteVideo/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Integer videoId) {
        try {
            boolean deleted = videoService.deleteVideo(videoId);
            if (deleted) return ResponseEntity.ok("Video deleted");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete failed");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");
        }
    }
}
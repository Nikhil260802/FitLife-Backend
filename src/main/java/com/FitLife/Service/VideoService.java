package com.FitLife.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    Resource accessVideo(String fileName, Integer userId, Integer courseId) throws Exception;

    List<String> getCourseVideosUrls(Integer courseId);

    boolean addVideo(MultipartFile[] files, Integer courseId) throws Exception;

    boolean deleteVideo(Integer videoId) throws Exception;

}

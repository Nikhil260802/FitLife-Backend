package com.FitLife.Dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseDto {
    private String courseName;
    private String description;
    private int price;
    private List<VideoDto> videos = new ArrayList<>();
}

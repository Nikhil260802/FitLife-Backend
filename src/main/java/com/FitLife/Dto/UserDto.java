package com.FitLife.Dto;

import com.FitLife.Entity.Course;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String userName;
    private String email;
    private String contactNumber;
    private String address;
    private List<Course> courses;
}

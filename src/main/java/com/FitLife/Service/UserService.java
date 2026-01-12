package com.FitLife.Service;

import com.FitLife.Dto.UserDto;
import com.FitLife.Entity.User;

import java.util.List;

public interface UserService {
    public UserDto getByUserId(int userId);

    public List<UserDto> getAllUsers();

    public boolean register(User user);

    public String verifyToken(String userName, String password);

    public boolean updateUser(int userId, User user);

    public boolean deleteUser(int userId);

    public void verifyOtp(String email, String otp);

}
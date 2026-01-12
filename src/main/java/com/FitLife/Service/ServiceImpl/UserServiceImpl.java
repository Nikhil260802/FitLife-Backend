package com.FitLife.Service.ServiceImpl;

import com.FitLife.Config.JwtService;
import com.FitLife.Dto.UserDto;
import com.FitLife.Entity.User;
import com.FitLife.Repository.UserRepository;
import com.FitLife.Service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    AuthenticationManager authManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto getByUserId(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? modelMapper.map(user, UserDto.class) : null;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }

    @Override
    public boolean register(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }

        String otp = generateOtp();

        user.setOtp(otp);
        user.setVerified(false);
        user.setRole("USER");
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);

        sendVerificationOtp(user.getEmail(), otp);

        return true;
    }

    @Override
    public String verifyToken(String userName, String password) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userName, password
                ));

        return auth.isAuthenticated() ?
                jwtService.generateToken(userName) :
                "fail";
    }

    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private void sendVerificationOtp(String email, String otp) {
        String subject = "Email Verification";
        String body = "Your verification otp is " + otp;
        emailService.sendMail(email, subject, body);
    }

    @Override
    public void verifyOtp(String email, String otp) {
        User req = userRepository.findByEmail(email);
        if (req == null) throw new RuntimeException("User not found");

        if (req.isVerified()) throw new RuntimeException("User already verified");

        if (!otp.equals(req.getOtp())) throw new RuntimeException("Invalid OTP");

        req.setVerified(true);
        req.setOtp(null);
        userRepository.save(req);
    }

    @Override
    public boolean updateUser(int userId, User updatedUser) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        user.setUserName(updatedUser.getUserName());
        user.setEmail(updatedUser.getEmail());
        user.setAddress(updatedUser.getAddress());
        user.setContactNumber(updatedUser.getContactNumber());
        user.setAge(updatedUser.getAge());
        user.setGender(updatedUser.getGender());

        userRepository.save(user);
        return true;
    }

    @Override
    public boolean deleteUser(int userId) {
        if (!userRepository.existsById(userId)) return false;
        userRepository.deleteById(userId);
        return true;
    }
}

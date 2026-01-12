package com.FitLife.Controller;

import com.FitLife.Dto.UserDto;
import com.FitLife.Entity.User;
import com.FitLife.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam("name") String userName, @RequestParam("password") String password) {
        String token = userService.verifyToken(userName, password);
        if (token.equals("fail")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        return ResponseEntity.ok(token);
    }

    @GetMapping("/public/getByUserId/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable int userId) {
        UserDto user = userService.getByUserId(userId);
        return user != null ?
                ResponseEntity.ok(user) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping("/admin/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        if (users.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No users found");
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        boolean success = userService.register(user);
        return success ?
                ResponseEntity.ok("User registered successfully") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
    }

    @PostMapping("/public/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            userService.verifyOtp(email, otp);
            return ResponseEntity.ok("User verified successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/public/updateUser/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody User user) {
        boolean success = userService.updateUser(userId, user);
        return success ?
                ResponseEntity.ok("User updated successfully") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User update failed");
    }

    @DeleteMapping("/public/deleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        boolean success = userService.deleteUser(userId);
        return success ?
                ResponseEntity.ok("User deleted successfully") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User deletion failed");
    }
}
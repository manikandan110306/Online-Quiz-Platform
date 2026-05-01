package com.examly.springapp.controller;

import com.examly.springapp.model.User;
import com.examly.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    /** Public signup — only TEACHER or STUDENT allowed. ADMIN is seeded or promoted. */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        // Enforce: only TEACHER or STUDENT can self-register
        String requestedRole = user.getRole();
        if (requestedRole == null || requestedRole.isBlank()
                || (!requestedRole.equalsIgnoreCase("TEACHER")
                    && !requestedRole.equalsIgnoreCase("STUDENT"))) {
            user.setRole("STUDENT"); // default safe role
        } else {
            user.setRole(requestedRole.toUpperCase());
        }
        userRepository.save(user);
        return ResponseEntity.ok("Signup successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .map(user -> {
                    if (user.getPassword().equals(loginRequest.getPassword())) {
                        return ResponseEntity.ok(user);
                    }
                    return ResponseEntity.status(401).body("Invalid credentials");
                })
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    /** Get all users — admin only in real apps, simplified here. */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/role/{role}")
    public List<User> getUsersByRole(@PathVariable String role) {
        return userRepository.findByRole(role.toUpperCase());
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    /** Admin promotes or demotes a user's role (TEACHER / STUDENT / ADMIN). */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newRole = body.get("role");
        if (newRole == null || (!newRole.equalsIgnoreCase("ADMIN")
                && !newRole.equalsIgnoreCase("TEACHER")
                && !newRole.equalsIgnoreCase("STUDENT"))) {
            return ResponseEntity.badRequest().body("Invalid role. Must be ADMIN, TEACHER, or STUDENT.");
        }
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body("User not found");
        User user = opt.get();
        user.setRole(newRole.toUpperCase());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    /** Admin deletes a user permanently. */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(404).body("User not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    /** Update profile — any authenticated user can update their own name/password. */
    @PutMapping("/users/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body("User not found");
        User user = opt.get();
        if (body.containsKey("name") && !body.get("name").isBlank()) {
            user.setName(body.get("name"));
        }
        if (body.containsKey("password") && !body.get("password").isBlank()) {
            user.setPassword(body.get("password"));
        }
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}


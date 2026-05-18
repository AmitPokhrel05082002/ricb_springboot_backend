package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.models.AgencyUserEntity;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String username) {
        return ResponseEntity.ok(authService.forgotPassword(username));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO dto) {
        return ResponseEntity.ok(authService.resetPassword(dto));
    }

    @PostMapping("/create-user")
    public ResponseEntity<String> createUser(@RequestBody CreateUserDTO dto) {
        return ResponseEntity.ok(authService.createUser(dto));
    }
    // ================= GET ALL USERS =================
    @GetMapping("/users")
    public ResponseEntity<List<AgencyUserEntity>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    // ================= GET USER BY ID =================
    @GetMapping("/user/{id}")
    public ResponseEntity<AgencyUserEntity> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    // ================= UPDATE USER =================
    @PutMapping("/update-user/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable String id,
            @RequestBody UpdateUserDTO dto
    ) {
        return ResponseEntity.ok(authService.updateUser(id, dto));
    }

    // ================= ACTIVATE / DEACTIVATE USER =================
    @PutMapping("/user-status/{id}")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable String id,
            @RequestParam Boolean isActive
    ) {
        return ResponseEntity.ok(authService.updateUserStatus(id, isActive));
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }
}
package bt.ricb.ricb_api.services;

import bt.ricb.ricb_api.models.AgencyUserEntity;
import bt.ricb.ricb_api.models.DTOs.*;
import bt.ricb.ricb_api.repository.AgencyUserRepository;
import bt.ricb.ricb_api.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private AgencyUserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    // ================= LOGIN =================
    public LoginResponseDTO login(LoginRequestDTO dto) {

        // 1. Check user exists
        AgencyUserEntity user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if user is active
        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }

        // 3. Check password
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        // 4. Generate token
        String token = jwtService.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getBranchId()
        );

        return new LoginResponseDTO(
                token,
                user.getId(),
                user.getFullName(),
                user.getRole().name(),
                user.getBranchId()
        );
    }

    // ================= FORGOT PASSWORD =================
    public String forgotPassword(String username) {

        // 1. Find user
        AgencyUserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Check email
        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            throw new RuntimeException(
                    "No email address registered for this user");
        }

        // 3. Generate temporary password
        String newPassword = generateRandomPassword(10);

        // 4. Email details
        String subject = "RICB Password Reset";

        String body =
                "Dear User,\n\n"
                        + "Your RICB password has been reset.\n\n"
                        + "Username: " + username + "\n"
                        + "New Password: " + newPassword + "\n\n"
                        + "Please login and change your password immediately.\n\n"
                        + "Regards,\n"
                        + "RICB";

        try {

            // 5. SEND EMAIL FIRST
            // If this fails, an exception will be thrown
            emailService.sendEmail(
                    user.getEmail(),
                    subject,
                    body,
                    null
            );

            // 6. Only update password if email was successfully sent
            user.setPassword(encoder.encode(newPassword));

            userRepo.save(user);

            return "Password reset successful. Check your email.";

        } catch (MessagingException | IOException e) {

            e.printStackTrace();

            // Password has NOT been changed
            throw new RuntimeException(
                    "Failed to send password reset email. "
                            + "Your password was not changed."
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Password reset failed. "
                            + "Your password was not changed."
            );
        }
    }

    // ================= RESET PASSWORD =================
    public String resetPassword(ResetPasswordDTO dto) {

        AgencyUserEntity user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        return "Password updated successfully";
    }

    // ================= PASSWORD GENERATOR =================
    private String generateRandomPassword(int length) {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    // ================= Create User =================

    public String createUser(CreateUserDTO dto) {

        if (userRepo.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Generate password
        String generatedPassword = generateRandomPassword(10);

        AgencyUserEntity user = new AgencyUserEntity();

        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setMobileNo(dto.getMobileNo());
        user.setPassword(encoder.encode(generatedPassword));
        user.setBranchId(dto.getBranchId());

        try {
            user.setRole(
                    AgencyUserEntity.Role.valueOf(dto.getRole().toUpperCase())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid role");
        }

        user.setIsActive(true);

        userRepo.save(user);

        // Send email
        String subject = "RICB Account Created";

        String body =
                "Your account has been created successfully.\n\n"
                        + "Username: " + user.getUsername() + "\n"
                        + "Password: " + generatedPassword + "\n\n"
                        + "Please change your password after login.";

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    subject,
                    body,
                    null
            );
        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }

        return "User created successfully";
    }

    // ================= GET ALL USERS =================
    public List<AgencyUserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    // ================= GET USER BY ID =================
    public AgencyUserEntity getUserById(String id) {

        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ================= UPDATE USER =================
    public String updateUser(String id, UpdateUserDTO dto) {

        AgencyUserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setMobileNo(dto.getMobileNo());
        user.setBranchId(dto.getBranchId());

        try {
            user.setRole(
                    AgencyUserEntity.Role.valueOf(dto.getRole().toUpperCase())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid role");
        }

        userRepo.save(user);

        return "User updated successfully";
    }

    // ================= ACTIVATE / DEACTIVATE USER =================
    public String updateUserStatus(String id, Boolean isActive) {

        AgencyUserEntity user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(isActive);

        userRepo.save(user);

        if (Boolean.TRUE.equals(isActive)) {
            return "User activated successfully";
        }

        return "User deactivated successfully";
    }
}
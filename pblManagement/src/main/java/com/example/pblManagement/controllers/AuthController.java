package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.common.LoginRequestDTO;
import com.example.pblManagement.model.dto.common.LoginResponseDTO;
import com.example.pblManagement.utils.SecurityUtils;
import com.example.pblManagement.utils.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SecurityUtils securityUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            // Authenticate using ID and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),  // Using email for auth
                            request.getPassword()
                    )
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            UserDetailsImpl userDetails = securityUtils.getCurrentUserDetails();
            String userId = userDetails.getId();
            String role = userDetails.getRole().name(); // Get enum name: "ADMIN", "LECTURER", "STUDENT"
            String email = userDetails.getEmail();

            return ResponseEntity.ok().body(new LoginResponseDTO(userId, email, role, "Login successful"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        UserDetailsImpl userDetails = securityUtils.getCurrentUserDetails();
        return ResponseEntity.ok().body(Map.of(
                "userId", userDetails.getId(),
                "email", userDetails.getEmail(),
                "role", userDetails.getRole().name(),
                "fullName", userDetails.getFullName()
        ));
    }
}

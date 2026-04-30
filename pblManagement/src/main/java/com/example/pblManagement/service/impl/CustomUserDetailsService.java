package com.example.pblManagement.service.impl;

import com.example.pblManagement.model.entities.Admin;
import com.example.pblManagement.model.entities.Lecturer;
import com.example.pblManagement.model.entities.Student;
import com.example.pblManagement.repositories.AdminRepository;
import com.example.pblManagement.repositories.LecturerRepository;
import com.example.pblManagement.repositories.StudentRepository;
import com.example.pblManagement.utils.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;
    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String id) throws UsernameNotFoundException {
        // Try to find user in Admin, Lecturer, Student tables
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin != null) {
            return new UserDetailsImpl(
                    admin.getId(),
                    admin.getEmail(),
                    admin.getFullName(),
                    admin.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        Lecturer lecturer = lecturerRepository.findById(id).orElse(null);
        if (lecturer != null) {
            return new UserDetailsImpl(
                    lecturer.getId(),
                    lecturer.getEmail(),
                    lecturer.getFullName(),
                    lecturer.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_LECTURER"))
            );
        }

        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            return new UserDetailsImpl(
                    student.getId(),
                    student.getEmail(),
                    student.getFullName(),
                    student.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
            );
        }

        throw new UsernameNotFoundException("User not found with Id: " + id);
    }
}

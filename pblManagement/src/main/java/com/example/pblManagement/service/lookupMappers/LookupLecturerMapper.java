package com.example.pblManagement.service.lookupMappers;

import com.example.pblManagement.model.entities.Lecturer;
import com.example.pblManagement.repositories.LecturerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LookupLecturerMapper {
    private final LecturerRepository lecturerRepository;

    // On PblClass creation, assign a lecturer
    @Named("mapLecturer")
    public Lecturer mapLecturer(String lecturerId) {
        if (lecturerId == null) return null;
        return lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found: " + lecturerId));
    }

    // Only extract the lecturer name
    @Named("mapLecturerName")
    public String mapLecturerName(Lecturer lecturer) {
        if (lecturer == null) return null;
        return lecturer.getFullName();
    }
}

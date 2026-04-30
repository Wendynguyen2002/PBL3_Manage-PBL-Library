package com.example.pblManagement.service.lookupMappers;

import com.example.pblManagement.model.entities.Major;
import com.example.pblManagement.repositories.MajorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LookupMajorMapper {
    private final MajorRepository majorRepository;

    // On creation, assign a major
    @Named("mapMajor")
    public Major mapMajor(String majorId) {
        if (majorId == null) return null;
        return majorRepository.findById(majorId)
                .orElseThrow(() -> new EntityNotFoundException("Major not found: " + majorId));
    }

    // Only extract the major name
    @Named("mapMajorName")
    public String mapMajorName(Major major) {
        if (major == null) return null;
        return major.getName();
    }
}

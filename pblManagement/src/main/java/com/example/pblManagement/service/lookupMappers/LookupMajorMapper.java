package com.example.pblManagement.service.lookupMappers;

import com.example.pblManagement.model.entities.Major;
import com.example.pblManagement.repositories.MajorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    @Named("mapMajors")
    public List<Major> mapMajors(List<String> majorIds) {
        if (majorIds == null) return null;
        return majorIds.stream()
                .map(id -> majorRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Major not found: " + id)))
                .collect(Collectors.toList());
    }

    // Only extract the major name
    @Named("mapMajorName")
    public String mapMajorName(Major major) {
        if (major == null) return null;
        return major.getName();
    }

    // Extract many major names
    @Named("mapMajorNames")
    public List<String> mapMajorNames(List<Major> majors) {
        if (majors == null) return null;
        return majors.stream()
                .map(Major::getName)
                .toList();
    }
}

package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.pbl.PblClassResponseDTO;
import com.example.pblManagement.model.dto.pbl.PblClassSummaryDTO;
import com.example.pblManagement.model.entities.Lecturer;
import com.example.pblManagement.model.entities.Major;
import com.example.pblManagement.model.entities.PblClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PblClassMapper {
    @Mapping(target = "lecturerName", source = "lecturer", qualifiedByName = "mapLecturerName")
    @Mapping(target = "majorNames", source = "majors", qualifiedByName = "mapMajorNames")
    PblClassResponseDTO toResponseDTO(PblClass pblClass);

    @Mapping(target = "lecturerName", source = "lecturer", qualifiedByName = "mapLecturerName")
    @Mapping(target = "majorNames", source = "majors", qualifiedByName = "mapMajorNames")
    PblClassSummaryDTO toSummaryDTO(PblClass pblClass);

    @Named("mapLecturerName")
    default String mapLecturerName(Lecturer lecturer) {
        if (lecturer == null) return null;
        return lecturer.getFullName();
    }

    // Extract many major names
    @Named("mapMajorNames")
    default List<String> mapMajorNames(List<Major> majors) {
        if (majors == null) return List.of();
        return majors.stream()
                .map(Major::getName)
                .toList();
    }
}

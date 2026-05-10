package com.example.pblManagement.service.lookupMappers;

import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.repositories.PblClassRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PblClassMapperForLecturer {
    // Only extract PBL classes' IDs
    @Named("mapPblClassIds")
    public List<String> toIds(List<PblClass> pblClasses) {
        if (pblClasses == null) return null;
        return pblClasses.stream()
                .map(PblClass::getId)
                .collect(Collectors.toList());
    }

    // Only extract PBL classes' names
    @Named("mapPblClassNames")
    public List<String> toNames(List<PblClass> pblClasses) {
        if (pblClasses == null) return null;
        return pblClasses.stream()
                .map(PblClass::getClassName)
                .collect(Collectors.toList());
    }
}

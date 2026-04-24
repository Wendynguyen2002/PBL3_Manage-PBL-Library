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
    private final PblClassRepository pblClassRepository;

//    @Named("mapPblClasses")
//    public List<PblClass> toEntities(List<String> pblClassIds) {
//        if (pblClassIds == null) return null;
//        if (pblClassIds.isEmpty()) return Collections.emptyList();
//
//        List<PblClass> foundClasses = pblClassRepository.findAllById(pblClassIds);
//
//        if (foundClasses.size() != pblClassIds.size()) {
//            List<String> foundIds = foundClasses.stream()
//                    .map(PblClass::getId)
//                    .toList();
//
//            List<String> missingIds = pblClassIds.stream()
//                    .filter(id -> !foundIds.contains(id))
//                    .toList();
//
//            throw new EntityNotFoundException("PBL Classes not found with IDs: " + missingIds);
//        }
//
//        return foundClasses;
//    }

    @Named("mapPblClassIds")
    public List<String> toIds(List<PblClass> pblClasses) {
        if (pblClasses == null) return null;
        return pblClasses.stream()
                .map(PblClass::getId)
                .collect(Collectors.toList());
    }

    @Named("mapPblClassNames")
    public List<String> toNames(List<PblClass> pblClasses) {
        if (pblClasses == null) return null;
        return pblClasses.stream()
                .map(PblClass::getClassName)
                .collect(Collectors.toList());
    }
}

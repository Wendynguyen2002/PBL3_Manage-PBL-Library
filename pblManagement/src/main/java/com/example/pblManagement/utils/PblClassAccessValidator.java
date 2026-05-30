package com.example.pblManagement.utils;

import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.repositories.PblClassRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PblClassAccessValidator {
    private final PblClassRepository pblClassRepository;

    public PblClass findClassAndValidateAccessAndReturnEntity(String pblClassId, Account account) {
        // Last line of defense?
        if (account == null || account.getRole() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        PblClass pblClass = pblClassRepository.findById(pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found with ID: " + pblClassId));

        return switch (account.getRole()) {
            case ADMIN -> pblClass;
            case LECTURER -> {
                if (pblClass.getLecturer() != null && pblClass.getLecturer().getId().equals(account.getId())) {
                    yield pblClass;
                }
                throw new AccessDeniedException("You don't have access to this class");
            }
            case STUDENT -> {
                if (pblClassRepository.isStudentEnrolledInClass(pblClassId, account.getId())) {
                    yield pblClass;
                }
                throw new AccessDeniedException("You are not enrolled in this class");
            }
        };
    }

    public void findClassAndValidateAccess(String pblClassId, Account account) {
        // Last line of defense?
        if (account == null || account.getRole() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        PblClass pblClass = pblClassRepository.findById(pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found with ID: " + pblClassId));

        switch (account.getRole()) {
            case ADMIN -> {
                // Admin can access all
            }
            case LECTURER -> {
                if (pblClass.getLecturer() != null && pblClass.getLecturer().getId().equals(account.getId())) {
                    return;
                }
                throw new AccessDeniedException("You don't have access to this class");
            }
            case STUDENT -> {
                if (pblClassRepository.isStudentEnrolledInClass(pblClassId, account.getId())) {
                    return;
                }
                throw new AccessDeniedException("You are not enrolled in this class");
            }
        }
    }
}

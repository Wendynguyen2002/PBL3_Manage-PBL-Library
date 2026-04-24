package com.example.pblManagement.service.impl;
import com.example.pblManagement.repositories.PblClassRepository;
import com.example.pblManagement.service.PblClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PblClassServiceImpl implements PblClassService {
    private final PblClassRepository pblClassRepository;
}

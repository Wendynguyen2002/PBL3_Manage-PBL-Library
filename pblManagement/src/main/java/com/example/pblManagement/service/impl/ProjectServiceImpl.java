package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.ProjectMapper;
import com.example.pblManagement.repositories.ProjectRepository;
import com.example.pblManagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;


}

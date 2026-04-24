package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.ProgressTaskMapper;
import com.example.pblManagement.repositories.ProgressTaskRepository;
import com.example.pblManagement.service.ProgressTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressTaskServiceImpl implements ProgressTaskService {
    private final ProgressTaskRepository progressTaskRepository;
    private final ProgressTaskMapper progressTaskMapper;
}

package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.PblGroupMapper;
import com.example.pblManagement.repositories.PblGroupRepository;
import com.example.pblManagement.service.PblGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PblGroupServiceImpl implements PblGroupService {
    private final PblGroupRepository pblGroupRepository;
    private final PblGroupMapper pblGroupMapper;


}

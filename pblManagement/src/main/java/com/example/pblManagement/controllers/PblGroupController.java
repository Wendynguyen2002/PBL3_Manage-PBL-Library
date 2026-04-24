package com.example.pblManagement.controllers;

import com.example.pblManagement.service.PblGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PblGroupController {
    private final PblGroupService pblGroupService;
}

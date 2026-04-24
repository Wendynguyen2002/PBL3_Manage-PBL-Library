package com.example.pblManagement.controllers;

import com.example.pblManagement.service.PblClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PblClassController {
    private final PblClassService pblClassService;
}

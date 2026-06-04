package com.example.pblManagement.model.dto.finalreport;

import lombok.*;
import org.springframework.core.io.Resource;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDownloadDTO {
    private Resource resource;

    private String originalFileName;

    private String fileType;

    private String title;
}

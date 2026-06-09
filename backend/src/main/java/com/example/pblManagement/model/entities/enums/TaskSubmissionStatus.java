package com.example.pblManagement.model.entities.enums;

public enum TaskSubmissionStatus {
    NOT_SUBMITTED, // Group has not submitted anything
    SUBMITTED, // Submitted, waiting for review
    REVIEWED // Lecturer has reviewed, waiting for feedback (in person)
}

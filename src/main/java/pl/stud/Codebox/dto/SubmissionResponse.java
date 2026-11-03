package pl.stud.Codebox.dto;

import pl.stud.Codebox.models.SubmissionStatus;

public record SubmissionResponse(Long id, String compilerOutput, SubmissionStatus submissionStatus) { }

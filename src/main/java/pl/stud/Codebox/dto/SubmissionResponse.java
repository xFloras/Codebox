package pl.stud.Codebox.dto;

import pl.stud.Codebox.models.Status;

public record SubmissionResponse(Long id, String compilerOutput, Status status) { }

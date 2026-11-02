package pl.stud.Codebox.dto;

import pl.stud.Codebox.models.Language;

public record SubmissionCreatedEvent(Long submissionId, Language language) { }

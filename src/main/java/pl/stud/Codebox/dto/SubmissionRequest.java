package pl.stud.Codebox.dto;

import pl.stud.Codebox.models.Language;

public record SubmissionRequest(Language language, String code) { }

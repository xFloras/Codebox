package pl.stud.Codebox.dto;

import pl.stud.Codebox.models.Difficulty;

import java.time.Instant;

public record ProblemResponse(
        Long id, String slug, String title, String description,
        Difficulty difficulty, int timeLimitMs, int memoryLimitMb, Instant createdAt
) {}

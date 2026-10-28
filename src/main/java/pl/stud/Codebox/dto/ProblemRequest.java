package pl.stud.Codebox.dto;

import pl.stud.Codebox.models.Difficulty;

public record ProblemRequest(
        String slug,
        String title,
        String description,
        Difficulty difficulty,
        Integer timeLimitMs,Integer memoryLimitMb) {}

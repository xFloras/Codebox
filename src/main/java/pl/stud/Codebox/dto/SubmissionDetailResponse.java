package pl.stud.Codebox.dto;

import pl.stud.Codebox.models.Language;
import pl.stud.Codebox.models.SubmissionStatus;
import pl.stud.Codebox.models.Verdict;

import java.time.Instant;
import java.util.List;

public record SubmissionDetailResponse(
        Long id,
        String slug,
        Language language,
        SubmissionStatus status,
        String compilerOutput,
        Integer totalTimeMs,
        Integer maxMemoryMb,
        Instant createdAt,
        Instant finishedAt,
        List<TestResultResponse> results
) {
    public record TestResultResponse(
            int position,
            Verdict verdict,
            Integer timeMs,
            Integer memoryKb,
            String input,
            String expectedOutput,
            String actualOutput
    ) { }
}

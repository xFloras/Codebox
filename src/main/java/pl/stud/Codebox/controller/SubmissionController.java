package pl.stud.Codebox.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.stud.Codebox.dto.SubmissionRequest;
import pl.stud.Codebox.dto.SubmissionResponse;
import pl.stud.Codebox.models.User;
import pl.stud.Codebox.service.SubmissionService;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping("/problems/{slug}/submissions")
    public ResponseEntity<SubmissionResponse> submit(
            @PathVariable String slug,
            @Valid @RequestBody SubmissionRequest submissionRequest,
            @AuthenticationPrincipal User user) {
        SubmissionResponse created = submissionService.submit(slug, submissionRequest, user.getId());
        URI location = URI.create("/api/submissions/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

}

package pl.stud.Codebox.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.stud.Codebox.dto.ProblemRequest;
import pl.stud.Codebox.dto.ProblemResponse;
import pl.stud.Codebox.service.ProblemService;

import java.net.URI;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {
    private final ProblemService problemService;
    //TODO: only admin can add problems(will be done after implementing jwt)
    @PostMapping
    public ResponseEntity<ProblemResponse> addProblem(@RequestBody ProblemRequest request) {
        ProblemResponse createdProblem = problemService.addProblem(request);
        /*
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{slug}")
                .buildAndExpand(createdProblem.slug())
                .toUri();
         */
        URI location = URI.create("/api/problems" + createdProblem.slug());
        return ResponseEntity.created(location).body(createdProblem);
    }

    @GetMapping("/{id:\\d+}")
    public ProblemResponse getById(@PathVariable ("id") Long id) {
        return problemService.getById(id);
    }

    @GetMapping("/{slug:\\D+}")
    public ProblemResponse getBySlug(@PathVariable ("slug") String slug) {
        return problemService.getBySlug(slug);
    }


}

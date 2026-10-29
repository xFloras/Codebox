package pl.stud.Codebox.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.stud.Codebox.dto.TestCaseRequest;
import pl.stud.Codebox.dto.TestCaseResponse;
import pl.stud.Codebox.service.TestCaseService;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestCaseController {
    private final TestCaseService testCaseService;

    @PostMapping("/problems/{slug}/test-cases")
    public ResponseEntity<TestCaseResponse> addTestCase(@PathVariable String slug, @Valid @RequestBody TestCaseRequest request){
        TestCaseResponse createdTestCase = testCaseService.addTestCase(slug, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTestCase);
    }

    @PatchMapping("/test-cases/{id}")
    public TestCaseResponse updateTestCase(@PathVariable Long id, @Valid @RequestBody TestCaseRequest request){
        return testCaseService.updateTestCase(id, request);
    }

    @DeleteMapping("/test-cases/{id}")
    public void deleteTestCase(@PathVariable Long id){
        testCaseService.deleteTestCase(id);
    }

    @GetMapping("/problems/{slug}/test-cases")
    public ResponseEntity<Iterable<TestCaseResponse>> getTestCases(@PathVariable String slug){
        return ResponseEntity.ok(testCaseService.getTestCases(slug));
    }

}

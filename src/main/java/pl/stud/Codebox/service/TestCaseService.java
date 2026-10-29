package pl.stud.Codebox.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.stud.Codebox.dto.TestCaseRequest;
import pl.stud.Codebox.dto.TestCaseResponse;
import pl.stud.Codebox.exceptions.ProblemNotFoundException;
import pl.stud.Codebox.exceptions.TestCaseNotFoundException;
import pl.stud.Codebox.models.Problem;
import pl.stud.Codebox.models.TestCase;
import pl.stud.Codebox.repository.ProblemRepository;
import pl.stud.Codebox.repository.TestCaseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;
    private final ProblemRepository problemRepository;

    @Transactional
    public TestCaseResponse addTestCase(String slug, TestCaseRequest request) {

        Problem problem = problemRepository.findBySlug(slug).orElseThrow(
                () -> new ProblemNotFoundException("Problem with slug " + slug + " does not exist."));

        TestCase tc = new TestCase();

        tc.setProblem(problem);
        tc.setInput(request.input());
        tc.setExpectedOutput(request.expectedOutput());
        tc.setSample(request.sample());
        tc.setPosition(testCaseRepository.findMaxPosition(problem.getId()) + 1);

        return toResponse(testCaseRepository.save(tc));
    }

    @Transactional
    public TestCaseResponse updateTestCase(Long id, TestCaseRequest request) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new TestCaseNotFoundException("Test case with id " + id + " does not exist."));
        if(request.input() != null) testCase.setInput(request.input());
        if(request.expectedOutput() != null)testCase.setExpectedOutput(request.expectedOutput());
        if(request.sample() != null)testCase.setSample(request.sample());
        return toResponse(testCase);
    }

    @Transactional(readOnly = true)
    public List<TestCaseResponse> getTestCases(String slug) {
        Problem problem = problemRepository.findBySlug(slug).orElseThrow(
                ()-> new ProblemNotFoundException("Problem with slug " + slug + " does not exist."));

        return testCaseRepository.findByProblemAndSampleTrue(problem)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    public void deleteTestCase(Long id) {
       if(!testCaseRepository.existsById(id)) {
           throw new TestCaseNotFoundException("Test case with id " + id + " does not exist.");
        }
        testCaseRepository.deleteById(id);
    }

    private TestCaseResponse toResponse(TestCase testCase) {
        return new TestCaseResponse(
                testCase.getInput(),
                testCase.getExpectedOutput(),
                testCase.getSample(),
                testCase.getPosition()
        );
    }

}

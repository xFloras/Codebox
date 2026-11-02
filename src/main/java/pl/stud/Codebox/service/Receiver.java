package pl.stud.Codebox.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.stud.Codebox.dto.SubmissionMessage;
import pl.stud.Codebox.dto.TestInput;
import pl.stud.Codebox.models.Problem;
import pl.stud.Codebox.models.Submission;
import pl.stud.Codebox.models.TestCase;
import pl.stud.Codebox.repository.SubmissionRepository;
import pl.stud.Codebox.repository.TestCaseRepository;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor

public class Receiver {

    private final SubmissionRepository submissionRepository;
    private final TestCaseRepository testCaseRepository;

    @RabbitListener(queues = "submissions.java")
    public void receive(SubmissionMessage message) throws IOException {
        log.info("Received message: {}", message.submissionId());

        Submission submission = submissionRepository.getReferenceById(message.submissionId());
        Problem problem = submission.getProblem();
        List<TestCase> testCases = testCaseRepository.findByProblemOrderByPosition(problem);


        SubmissionPreparer.prepareSubmission(submission.getId(), submission.getCode(), toListInput(testCases));


    }

    private TestInput toInput(TestCase testCase) {
        return new TestInput(testCase.getId(), testCase.getPosition(), testCase.getInput());
    }


    private List<TestInput> toListInput(List<TestCase> testCases) {
        return testCases.stream().map(this::toInput).toList();
    }

}

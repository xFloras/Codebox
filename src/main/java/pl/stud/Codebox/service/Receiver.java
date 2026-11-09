package pl.stud.Codebox.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import pl.stud.Codebox.dto.RunResult;
import pl.stud.Codebox.dto.SubmissionMessage;
import pl.stud.Codebox.dto.TestInput;
import pl.stud.Codebox.dto.TestOutcome;
import pl.stud.Codebox.models.*;
import pl.stud.Codebox.repository.SubmissionRepository;
import pl.stud.Codebox.repository.SubmissionResultRepository;
import pl.stud.Codebox.repository.TestCaseRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor

public class Receiver {
    private record Loaded(Submission submission, List<TestCase> testCases, int memoryLimitMb, int timeLimitMs) {}

    private final SubmissionRepository submissionRepository;
    private final TestCaseRepository testCaseRepository;
    private final SubmissionResultRepository submissionResultRepository;
    private final CodeRunner codeRunner;
    private final TransactionTemplate transactionTemplate;

    @RabbitListener(queues = "submissions.java")
    public void receive(SubmissionMessage message) {
        long id = message.submissionId();

        Loaded loaded = transactionTemplate.execute(s -> {
            Submission submission = submissionRepository.findById(id).orElseThrow();
            if(submission.getStatus() != SubmissionStatus.PENDING) return null;
            submission.setStatus(SubmissionStatus.RUNNING);
            submission.setStartedAt(Instant.now());
            Problem problem = submission.getProblem();
            List<TestCase> testCases = testCaseRepository.findByProblemOrderByPosition(problem);
            return new Loaded(submission, testCases, problem.getMemoryLimitMb(), problem.getTimeLimitMs());
        });

        if(loaded == null) return;

        Submission submission = loaded.submission();
        List<TestCase> testCases = loaded.testCases();
        Path dir = null;

        try {
            dir = SubmissionPreparer.prepareSubmission(id, submission.getCode(), toListInput(testCases));
            RunResult result = codeRunner.runSubmission(dir, loaded.memoryLimitMb(), loaded.timeLimitMs(), testCases.size());
            transactionTemplate.executeWithoutResult(s -> {
                storeResults(id, testCases, result, loaded.timeLimitMs());
            });
        } catch (Exception e) {
            log.error("Judging {} failed", id, e);
            transactionTemplate.executeWithoutResult(s -> {
                submissionRepository.findById(id).ifPresent(sub -> {
                        sub.setStatus(SubmissionStatus.INTERNAL_ERROR);
                        sub.setFinishedAt(Instant.now());
                });
            });
        } finally {
            if(dir != null) try {SubmissionPreparer.cleanUp(dir);} catch (IOException ignored) {
                log.warn("Failed to clean up submission directory {}", dir);
            }
        }




    }

    private void storeResults(long id, List<TestCase> testCases, RunResult result, long timeLimitMs) {
        Submission submission = submissionRepository.findById(id).orElseThrow();
        submission.setFinishedAt(Instant.now());

        if(!result.compiled()){
            submission.setStatus(SubmissionStatus.COMPILE_ERROR);
            submission.setCompilerOutput(result.compilerOutput());
            return;
        }

        Map<Integer, TestOutcome> byOutcome = result.tests().stream()
                .collect(Collectors.toMap(TestOutcome::position, t -> t));

        boolean allPassed = true;
        long maxTime = 0;

        for(TestCase tc : testCases) {
            TestOutcome t = byOutcome.get(tc.getPosition());
            Verdict v = (t == null) ? Verdict.TLE : verdictFor(t, tc, timeLimitMs);
            allPassed &= v == Verdict.AC;

            SubmissionResult r = new SubmissionResult();
            r.setSubmission(submission);
            r.setTestCase(tc);
            r.setStatus(v);
            if(t != null){
                maxTime = Math.max(maxTime, t.timeMs());
                r.setTimeMs((int) t.timeMs());
                r.setActualOutput(t.stdout());
            }
            submissionResultRepository.save(r);
        }
        submission.setTotalTimeMs((int) maxTime);
        submission.setStatus(allPassed ? SubmissionStatus.SUCCESS : SubmissionStatus.FAILED);

    }

    private static Verdict verdictFor(TestOutcome t, TestCase tc, long timeLimitMs) {
        if(t.timeMs() >= timeLimitMs) return Verdict.TLE;
        if(t.exitCode() == 137 || t.stderr().contains("OutOfMemoryError")) return Verdict.MLE;
        if(t.exitCode() != 0) return Verdict.RE;
        return normalize(t.stdout()).equals(normalize(tc.getExpectedOutput())) ? Verdict.AC : Verdict.WA;
    }

    private static String normalize(String s) {
        return s.lines().map(String::stripTrailing).collect(Collectors.joining("\n")).strip();
    }

    private TestInput toInput(TestCase testCase) {
        return new TestInput(testCase.getId(), testCase.getPosition(), testCase.getInput());
    }


    private List<TestInput> toListInput(List<TestCase> testCases) {
        return testCases.stream().map(this::toInput).toList();
    }

}

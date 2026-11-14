package pl.stud.Codebox.service;


import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.stud.Codebox.dto.SubmissionCreatedEvent;
import pl.stud.Codebox.dto.SubmissionDetailResponse;
import pl.stud.Codebox.dto.SubmissionRequest;
import pl.stud.Codebox.dto.SubmissionResponse;
import pl.stud.Codebox.exceptions.ProblemNotFoundException;
import pl.stud.Codebox.exceptions.SubmissionNotFoundException;
import pl.stud.Codebox.models.*;
import pl.stud.Codebox.repository.ProblemRepository;
import pl.stud.Codebox.repository.SubmissionRepository;
import pl.stud.Codebox.repository.SubmissionResultRepository;
import pl.stud.Codebox.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubmissionService {
        private final SubmissionRepository submissionRepository;
        private final ProblemRepository problemRepository;
        private final UserRepository userRepository;

        private final ApplicationEventPublisher events;
    private final SubmissionResultRepository submissionResultRepository;

    @Transactional
        public SubmissionResponse submit(String slug, SubmissionRequest submissionRequest, Long userId) {
                Problem problem = problemRepository.findBySlug(slug).orElseThrow(
                        () -> new ProblemNotFoundException("Problem with slug " + slug + " does not exist.")
                );

            Submission submission = new Submission();
            submission.setUser(userRepository.getReferenceById(userId));
            submission.setProblem(problem);
            submission.setLanguage(submissionRequest.language());
            submission.setCode(submissionRequest.code());

            Submission savedSubmission = submissionRepository.save(submission);
            events.publishEvent(new SubmissionCreatedEvent(savedSubmission.getId(), submissionRequest.language()));

            return toResponse(savedSubmission);
        }

        private SubmissionResponse toResponse(Submission submission) {
                return new SubmissionResponse(submission.getId(), submission.getCompilerOutput(), submission.getStatus());
        }

        @Transactional(readOnly = true)
        public SubmissionDetailResponse getDetail(Long id, User requester) {
            Submission s = submissionRepository.findById(id).orElseThrow(() -> new SubmissionNotFoundException("Submission not found"));

            if(!s.getUser().getId().equals(requester.getId()) && requester.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("You are not allowed to access this submission");
            }

            List<SubmissionDetailResponse.TestResultResponse> results = submissionResultRepository
                    .findBySubmissionOrderByTestCasePosition(s)
                    .stream()
                    .map(r -> toResultResponse(r, requester))
                    .toList();

            return new SubmissionDetailResponse(s.getId(), s.getProblem().getSlug(), s.getLanguage(), s.getStatus(),
                    s.getCompilerOutput(), s.getTotalTimeMs(), s.getMaxMemoryMb(), s.getCreatedAt(), s.getFinishedAt(), results);

        }

        private SubmissionDetailResponse.TestResultResponse toResultResponse(SubmissionResult r, User requester) {
            TestCase tc = r.getTestCase();
            boolean reveal = tc.getSample() || requester.getRole() == Role.ADMIN;
            return new SubmissionDetailResponse.TestResultResponse(tc.getPosition(),
                    r.getStatus(),
                    r.getTimeMs(),
                    r.getMemoryMb(),
                    reveal ? tc.getInput() : null,
                    reveal ? tc.getExpectedOutput() : null,
                    reveal ? r.getActualOutput() : null
                    );
        }

}

package pl.stud.Codebox.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.stud.Codebox.dto.SubmissionCreatedEvent;
import pl.stud.Codebox.dto.SubmissionRequest;
import pl.stud.Codebox.dto.SubmissionResponse;
import pl.stud.Codebox.exceptions.ProblemNotFoundException;
import pl.stud.Codebox.models.Problem;
import pl.stud.Codebox.models.Submission;
import pl.stud.Codebox.repository.ProblemRepository;
import pl.stud.Codebox.repository.SubmissionRepository;
import pl.stud.Codebox.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SubmissionService {
        private final SubmissionRepository submissionRepository;
        private final ProblemRepository problemRepository;
        private final UserRepository userRepository;

        private final ApplicationEventPublisher events;

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



}

package pl.stud.Codebox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.stud.Codebox.repository.SubmissionRepository;

@Service
@RequiredArgsConstructor
public class SubmissionService {
        private final SubmissionRepository submissionRepository;


}

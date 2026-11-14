package pl.stud.Codebox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.stud.Codebox.models.Submission;
import pl.stud.Codebox.models.SubmissionResult;

import java.util.List;

public interface SubmissionResultRepository extends JpaRepository<SubmissionResult, Long> {
    List<SubmissionResult> findBySubmissionOrderByTestCasePosition(Submission submission);
}

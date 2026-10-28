package pl.stud.Codebox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.stud.Codebox.models.Submission;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByUserId(Long userId);
    Optional<Submission> findByProblemId(Long problemId);
}

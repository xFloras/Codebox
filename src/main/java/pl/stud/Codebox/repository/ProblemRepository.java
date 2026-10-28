package pl.stud.Codebox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.stud.Codebox.models.Problem;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Optional<Problem> findById(Long id);
    Optional<Problem> findBySlug(String slug);
    boolean existsBySlug(String slug);
    //TODO: add findByTitle and findByDifficulty
    List<Problem> findByTitleContainingIgnoreCase(String title);
    List<Problem> findByDifficulty(String difficulty);

}

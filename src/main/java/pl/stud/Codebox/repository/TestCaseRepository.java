package pl.stud.Codebox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.stud.Codebox.models.Problem;
import pl.stud.Codebox.models.TestCase;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    List<TestCase> findByProblemAndSampleTrue(Problem problem);

    /*
    * Returns the max position of the test cases with given problemId
    * Used to calculate the new position of the new test case
     */
    @Query("SELECT COALESCE(MAX(t.position), 0) FROM TestCase t WHERE t.problem.id = :problemId")
    int findMaxPosition(@Param("problemId") Long problemId);
}

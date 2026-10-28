package pl.stud.Codebox.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.stud.Codebox.dto.ProblemRequest;
import pl.stud.Codebox.dto.ProblemResponse;
import pl.stud.Codebox.exceptions.ProblemNotFoundException;
import pl.stud.Codebox.models.Problem;
import pl.stud.Codebox.repository.ProblemRepository;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemResponse addProblem(ProblemRequest request) {
        Problem problem = new Problem();
        problem.setSlug(request.slug());
        problem.setTitle(request.title());
        problem.setDescription(request.description());
        problem.setDifficulty(request.difficulty());
        problem.setTimeLimitMs(request.timeLimitMs());
        problem.setMemoryLimitMb(request.memoryLimitMb());

        problemRepository.save(problem);

        return toResponse(problem);
    }
    @Transactional(readOnly = true)
    public ProblemResponse getById(Long id) {
        return problemRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->
                     new ProblemNotFoundException("Problem with id " + id + " does not exist.")
                );
    }

    @Transactional(readOnly = true)
    public ProblemResponse getBySlug(String slug) {
        return problemRepository.findBySlug(slug)
                .map(this::toResponse)
                .orElseThrow(() ->
                     new ProblemNotFoundException("Problem with slug " + slug + " does not exist.")
                );
    }


    private ProblemResponse toResponse(Problem problem) {
        return new ProblemResponse(problem.getId(), problem.getSlug(), problem.getTitle(), problem.getDescription(),
                problem.getDifficulty(), problem.getTimeLimitMs(), problem.getMemoryLimitMb(), problem.getCreatedAt());
    }
}

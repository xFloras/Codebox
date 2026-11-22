package pl.stud.Codebox;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.stud.Codebox.dto.ProblemResponse;
import pl.stud.Codebox.dto.TestCaseResponse;
import pl.stud.Codebox.repository.SubmissionRepository;

import java.time.Duration;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class JudgingPipelineTest extends AbstractIntegrationTest {
    @Autowired
    SubmissionRepository submissionRepository;

    private static final String A_PLUS_B = """
        import java.util.*;
        public class Main { public static void main(String[] a) {
            Scanner s = new Scanner(System.in);
            while (s.hasNextLong()) System.out.println(s.nextLong() + s.nextLong());
        } }
        """;


    @Test
    void correctSolutionIsAccepted() throws Exception{
        String admin = auth.adminToken();
        ProblemResponse problem = problems.createProblem(admin, 2000, 256);
        String slug = problem.slug();
        TestCaseResponse testCase1 = problems.addTestCase(admin, slug, "1 1", "2", true);
        TestCaseResponse testCase2 = problems.addTestCase(admin, slug, "1 2", "3", false);
        String user = auth.userToken();

        String body = mvc.perform(post("/api/problems/{slug}/submissions", slug)
                        .header("Authorization", "Bearer " + user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("language", "JAVA", "code", A_PLUS_B))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        long id = json.readTree(body).get("id").asLong();

        await().atMost(Duration.ofSeconds(60)).pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> mvc.perform(get("/api/submissions/" + id)
                        .header("Authorization", "Bearer " + user))
                        .andExpect(jsonPath("$.status").value("SUCCESS"))
                        .andExpect(jsonPath("$.results.length()").value(2))
                        .andExpect(jsonPath("$.results[*].verdict", everyItem(is("AC")))));

    }

}

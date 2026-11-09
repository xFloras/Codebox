package pl.stud.Codebox.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.stud.Codebox.dto.RunResult;
import pl.stud.Codebox.dto.TestOutcome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Component
public class CodeRunner {

    private final String scriptPath;

    public CodeRunner(@Value("${codebox.runner.script:./runSubmission.sh}") String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public RunResult runSubmission(Path submissionPath, int memoryLimitMb, int timeLimitMs, int testCount) throws IOException, InterruptedException {
        String name = "judge_" + submissionPath.getFileName();

        int timeLimitS = Math.max(1, (int) Math.ceil(timeLimitMs / 1000.0));

        ProcessBuilder pb = new ProcessBuilder(
                scriptPath,
                submissionPath.toAbsolutePath().toString(),
                memoryLimitMb + "m",
                "1.0",
                String.valueOf(timeLimitS)
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        CompletableFuture<String> log = CompletableFuture.supplyAsync(() -> {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } catch (IOException e) {}
                return "";
            });

            long budgetMs = (long) testCount * timeLimitS * 1000 + 10_000;

            if(!process.waitFor(budgetMs, TimeUnit.MILLISECONDS)) {
                killContainer(name);
                process.destroyForcibly();
                process.waitFor();
            }
            String scriptLog = log.join();

            return readOutcome(submissionPath.resolve("out"), process.exitValue(), scriptLog);
    }

    private void killContainer(String name) {
        try {
            new ProcessBuilder("docker", "kill", name).redirectErrorStream(true).start().waitFor(5, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException ignored) {}
    }
    private RunResult readOutcome(Path out, int scriptExit, String scriptLog) throws IOException {
        String compilerOutput = readIfExists(out.resolve("compile.txt"));
        if(scriptExit != 0) {
            return new RunResult(false, compilerOutput, List.of());
        }
        List<TestOutcome> tests = new ArrayList<>();
        try (var files = Files.list(out)) {
            List<Integer> positions = files
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".code"))
                    .map(n -> Integer.parseInt(n.substring(0, n.length() - 5)))
                    .sorted()
                    .toList();
            for(int position : positions) {
                tests.add(new TestOutcome(
                        position,
                        Integer.parseInt(readIfExists(out.resolve(position + ".code")).trim()),
                        Long.parseLong(readIfExists(out.resolve(position + ".time")).trim()),
                        readIfExists(out.resolve(position + ".out")),
                        readIfExists(out.resolve(position + ".err"))
                ));
            }
        }
        return new RunResult(true, compilerOutput, tests);
    }

    private static String readIfExists(Path p) throws IOException {
        return Files.exists(p) ? Files.readString(p) : "";
    }

}

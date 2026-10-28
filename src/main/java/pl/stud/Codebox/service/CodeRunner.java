package pl.stud.Codebox.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;



public class CodeRunner {

    public static void runSubmission(Path submissionPath) throws RuntimeException, InterruptedException{
        ProcessBuilder pb = new ProcessBuilder(
                "./runSubmission.sh",
                submissionPath.toAbsolutePath().toString(),
                "128m",
                "1.0"
        );

        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            CompletableFuture<String> outputFuture = CompletableFuture.supplyAsync(() -> {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } catch (IOException e) {}
                return sb.toString();
            });

            if(!process.waitFor(4, java.util.concurrent.TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new RuntimeException("Time limit exceeded");
            }
            int exitCode = process.exitValue();

            String resultText = outputFuture.toString().trim();

            SubmissionPreparer.cleanUp(submissionPath);

            System.out.println("--- DOCKER RAW OUTPUT ---");
            System.out.println(resultText);
            System.out.println("-------------------------");

            // Analiza wyników
            if (exitCode == 0 && resultText.contains("ALL_PASSED")) {
                System.out.println("VERDICT: Accepted (AC)");
            } else if (exitCode == 137) {
                System.out.println("VERDICT: Memory Limit Exceeded (MLE)");
            } else if (resultText.contains("error: ") || resultText.contains("javac")) {
                System.out.println(resultText);
                System.out.println("VERDICT: Compilation Error (CE)");
            } else if (resultText.contains("TEST_FAILED")) {
                System.out.println("VERDICT: Wrong Answer (WA)");
            } else {
                System.out.println("VERDICT: Runtime Error (RE)");
                System.out.println(resultText);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

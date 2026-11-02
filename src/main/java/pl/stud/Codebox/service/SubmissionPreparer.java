package pl.stud.Codebox.service;


import pl.stud.Codebox.dto.TestInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;


public class SubmissionPreparer {

    private static final Path SUBMISSIONS_ROOT = Path.of(System.getProperty("java.io.tmpdir"));

    public static Path prepareSubmission(long submissionId, String userCode, List<TestInput> tests) throws IOException {
        Path dir = SUBMISSIONS_ROOT.resolve("submission_" + submissionId);
        cleanUp(dir);
        Files.createDirectories(dir.resolve("inputs"));
        Files.createDirectories(dir.resolve("out"));

        Files.writeString(dir.resolve("Main.java"), userCode);


        for(TestInput input : tests) {
            Files.writeString(dir.resolve("inputs").resolve(input.position() + ".in"), input.input());
        }

        return dir;

    }



    public static void cleanUp(Path tempDir) throws IOException {
        if(Files.exists(tempDir)) {
            try (var stream = Files.walk(tempDir)) {
                stream.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }


}



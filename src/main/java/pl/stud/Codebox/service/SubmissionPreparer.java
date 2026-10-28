package pl.stud.Codebox.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;


public class SubmissionPreparer {

    public static Path prepareSubmission(String userCode, String testCode) throws IOException {
        Path tempDir = Files.createTempDirectory("submission_");

        Path solutionPath = tempDir.resolve("Solution.java");
        Path mainPath = tempDir.resolve("Main.java");

        Files.writeString(solutionPath, userCode);
        Files.writeString(mainPath, testCode);
        return tempDir;

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



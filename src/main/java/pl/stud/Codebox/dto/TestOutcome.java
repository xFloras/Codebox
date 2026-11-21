package pl.stud.Codebox.dto;

public record TestOutcome(int position, int exitCode, long timeMs, long memoryMb, String stdout, String stderr) { }

package pl.stud.Codebox.dto;

public record TestOutcome(int position, int exitCode, long timeMs, String stdout, String stderr) { }

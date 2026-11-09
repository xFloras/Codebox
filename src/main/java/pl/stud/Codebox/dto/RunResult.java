package pl.stud.Codebox.dto;

import java.util.List;

public record RunResult(boolean compiled, String compilerOutput, List<TestOutcome> tests) { }

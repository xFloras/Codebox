package pl.stud.Codebox.dto;

public record TestCaseResponse(String input, String expectedOutput, Boolean sample, Integer position) { }

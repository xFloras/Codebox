package pl.stud.Codebox.dto;

public record UserLoginResponse(String token, long expiresIn) { }

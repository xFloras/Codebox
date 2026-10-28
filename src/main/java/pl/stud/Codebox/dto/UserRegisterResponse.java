package pl.stud.Codebox.dto;

import pl.stud.Codebox.models.Role;

public record UserRegisterResponse(String username, String email, Role role) { }

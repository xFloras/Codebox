package pl.stud.Codebox.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.stud.Codebox.dto.UserLoginRequest;
import pl.stud.Codebox.dto.UserLoginResponse;
import pl.stud.Codebox.dto.UserRegisterRequest;
import pl.stud.Codebox.dto.UserRegisterResponse;
import pl.stud.Codebox.service.AuthenticationService;
import pl.stud.Codebox.service.JwtService;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponse> signup(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        UserRegisterResponse userResponse = authenticationService.signup(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@Valid @RequestBody UserLoginRequest userLoginRequest){
        return authenticationService.authenticate(userLoginRequest);
    }

}

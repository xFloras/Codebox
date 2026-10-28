package pl.stud.Codebox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.stud.Codebox.dto.UserLoginRequest;
import pl.stud.Codebox.dto.UserLoginResponse;
import pl.stud.Codebox.dto.UserRegisterRequest;
import pl.stud.Codebox.dto.UserRegisterResponse;
import pl.stud.Codebox.exceptions.UserAlreadyExistsException;
import pl.stud.Codebox.exceptions.UserNotFoundException;
import pl.stud.Codebox.models.Role;
import pl.stud.Codebox.models.User;
import pl.stud.Codebox.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public UserRegisterResponse signup(UserRegisterRequest userRegister) {
        if (userRepository.existsByUsername(userRegister.username()) || userRepository.existsByEmail(userRegister.email())) {
            throw new UserAlreadyExistsException("Username or email already taken");
        }
        User user = new User();
        user.setUsername(userRegister.username());
        user.setEmail(userRegister.email());
        user.setPassword(passwordEncoder.encode(userRegister.password()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return new UserRegisterResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    public UserLoginResponse authenticate(UserLoginRequest userLogin) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.username(), userLogin.password()));
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return new UserLoginResponse(token, jwtService.getExpirationMs());
    }


}

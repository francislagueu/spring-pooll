package francislagueu.task.controllers;

import francislagueu.task.exception.AppException;
import francislagueu.task.models.Role;
import francislagueu.task.models.RoleName;
import francislagueu.task.models.User;
import francislagueu.task.payload.ApiResponse;
import francislagueu.task.payload.AuthenticationResponse;
import francislagueu.task.payload.Login;
import francislagueu.task.payload.Register;
import francislagueu.task.repositories.RoleRepository;
import francislagueu.task.repositories.UserRepository;
import francislagueu.task.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody Login login){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        login.getUsernameOrEmail(),
                        login.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody Register register){
        if(userRepository.existsByUsername(register.getUsername()))
            return new ResponseEntity(new ApiResponse(false, "Username is already taken"), HttpStatus.BAD_REQUEST);
        if(userRepository.existsByEmail(register.getEmail()))
            return new ResponseEntity(new ApiResponse(false, "Email address already in use!"), HttpStatus.BAD_REQUEST);

        User user = new User(register.getFirstName(), register.getLastName(), register.getEmail(), register.getPassword(), register.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(()-> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

}

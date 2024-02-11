package com.example.stageback.controllers;

import com.example.stageback.dto.AuthenticationRequest;
import com.example.stageback.dto.RegistrationRequest;
import com.example.stageback.servicesImplementation.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class AuthenticationController {
    private final AuthenticationService authenticationService;



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request, BindingResult bindingResult)  {
        if (bindingResult.hasErrors()) {
            // Renvoyer les erreurs de validation
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        if (authenticationService.register(request) == null)
        {
            return ResponseEntity.ok("Email existant!");
        }

        else
        {
            return ResponseEntity.ok("Compte crée avec succès!\nVeuillez l'activer via\n"+request.getEmail()+"!!");
        }

    }
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    @PostMapping( "/confirm")
    public ResponseEntity<?> confirm(@RequestBody String token) {
        return ResponseEntity.ok(authenticationService.confirmEmailToken(token));
    }

    @PostMapping("/resetPassword")
    public String requestPass(@RequestParam String email)
    {
        return authenticationService.requestResetPassword(email);
    }

    @PostMapping("/confirmPassword")
    public String confirmPass (@RequestParam String email,

                               @RequestParam String pass)
    {  return authenticationService.passwordResetConfirm(email,pass);
    }

}

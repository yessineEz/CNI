package com.example.stageback.servicesImplementation;

import com.example.stageback.dto.AuthenticationRequest;
import com.example.stageback.dto.RegistrationRequest;
import com.example.stageback.entities.ConfirmationToken;
import com.example.stageback.entities.JwtToken;
import com.example.stageback.entities.User;
import com.example.stageback.enumerations.Role;
import com.example.stageback.enumerations.TokenType;
import com.example.stageback.repositories.JwtTokenRepo;
import com.example.stageback.repositories.UserRepo;
import com.example.stageback.services.EmailSender;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final EmailSender emailSender;
    private final UserService userService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenRepo jwtTokenRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService tokenService;


    public String authenticate(AuthenticationRequest request) {
        var user = userRepo.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "Pas de compte enregistré avec cet e-mail!";
        }

         if (!user.getEmailVerif()) {
            return ("E-mail pas encore vérifié");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.err.println(user.getPassword());
            System.err.println(passwordEncoder.encode(request.getPassword()));
            return ("Vérifiez votre mot de passe SVP");
        }


        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                     request.getEmail(),
                     request.getPassword()
             ));

             var jwtTokenString = "";
             jwtTokenString = jwtService.generateJwtToken(user);
             revokeAllUserTokens(user);
             saveJwtToken(user, jwtTokenString);


        return jwtTokenString;
    }


    private void saveJwtToken(User user, String jwtTokenString) {
        var jwtToken = JwtToken.builder()
                .token(jwtTokenString)
                .user(user)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        jwtTokenRepo.save(jwtToken);
    }

    public User register(@Valid RegistrationRequest request) {

        User user2 = userRepo.findByEmail2(request.getEmail());
        if (user2!= null)
        {
            return null;
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .emailVerif(false)
                .build();
        userRepo.save(user);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                user);
        tokenService.saveConfirmationToken(confirmationToken);
        String link = "http://localhost:4200/mail-verif?token="+token;
        emailSender.send(request.getEmail(),buildEmail2(user,link));
        var jwtTokenString = jwtService.generateJwtToken(user);


        return user;
    }






    private String buildEmail2(User user,String link)
    {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Titre de l'email</title>\n" +
                "    <style>\n" +
                "      /* Styles pour l'arrière-plan uni */\n" +
                "      body {\n" +
                "        background-color: #F5F5F5;\n" +
                "        margin: 0;\n" +
                "        padding: 0;    \n" +
                "\tfont-family: Arial, sans-serif;\n" +
                "\n" +
                "      }\n" +
                "      /* Styles pour le conteneur principal */\n" +
                "      .container {\n" +
                "        max-width: 600px;\n" +
                "        margin: 0 auto;\n" +
                "        background-color: #FFFFFF;\n" +
                "        padding: 20px;\n" +
                "        height: 100vh;\n" +
                "        display: flex;\n" +
                "        flex-direction: column;\n" +
                "        justify-content: center;\n" +
                "      }\n" +
                "      /* Styles pour le logo de l'entreprise */\n" +
                "      .logo {\n" +
                "        display: block;\n" +
                "        margin: -20px auto 20px;\n" +
                "        width: 100px;\n" +
                "        height: auto;\n" +
                "      }\n" +
                "      /* Styles pour le corps du texte */\n" +
                "      .text {\n" +
                "        text-align: center;\n" +
                "      }\n" +
                "      /* Styles pour le bouton animé */\n" +
                "      .button {\n" +
                "        display: inline-block;\n" +
                "        font-size: 16px;\n" +
                "        font-weight: bold;\n" +
                "        color: #3CAEA3;\n" +
                "        background-color: transparent;\n" +
                "        border-radius: 5px;\n" +
                "        padding: 10px 20px;\n" +
                "        border: 2px solid #3CAEA3;\n" +
                "        text-decoration: none;\n" +
                "        transition: all 0.5s ease;\n" +
                "      }\n" +
                "      .button:hover {\n" +
                "        background-color: #3CAEA3;\n" +
                "        color: #FFFFFF;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div class=\"container\">\n" +
                "      <img src=\"https://i.ibb.co/bHQGYdB/logo.jpg\" alt=\"Bienvenue\" padding-left=\"60%\" height=\"200px\" width=\"300px\">\n" +
                "<br>     \n" +
                " <div class=\"text\">\n" +
                "        <h1 style=\"color : #3CAEA3;\">Bonjour "+user.getFirstName()+" "+user.getLastName()+"</h1>\n" +
                "        <h3>Merci pour votre inscription dans notre application. Appuyer sur lien suivant pour activer votre compte:</h3>\n" +
                "<p style=\"color : red\">Le lien expire dans 15 minutes.</p>\n" +
                "        <p><a href="+link+" class=\"button\">Lien De Vérification</a></p>\n" +
                "\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>\n";
    }


    @Transactional
    public String confirmEmailToken(String token) {
        System.err.println(token);
        ConfirmationToken confirmationToken = tokenService.getToken(token).get();

        if (confirmationToken == null)
        {
            return "token not found";
        }

        if (confirmationToken.getConfirmedAt() != null) {
            return ("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            String token2 = UUID.randomUUID().toString();

            ConfirmationToken confirmationToken2 = new ConfirmationToken(token2, LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(1),
                    confirmationToken.getUser());
            tokenService.saveConfirmationToken(confirmationToken2);
            String link = "http://localhost:4200/mail-verif?token="+token2;
            emailSender.send(confirmationToken.getUser().getEmail(),buildEmail2(confirmationToken.getUser(),link));
            return "email expired a new Email is sent!";
        }

        tokenService.setConfirmedAt(token);
        //userService.enableAppUser(
        //      confirmationToken.getUser().getEmail());
        User user = confirmationToken.getUser();
        user.setEmailVerif(true);
        userRepo.save(user);
        userService.enableAppUser(confirmationToken.getUser().getEmail());
        return "Email confirmed ";
    }







    public void revokeAllUserTokens(User user) {
        var validUserTokens = jwtTokenRepo.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jwtTokenRepo.saveAll(validUserTokens);
    }





    private String buildEmailVerif(String token,User user)
    {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Titre de l'email</title>\n" +
                "    <style>\n" +
                "      /* Styles pour l'arrière-plan uni */\n" +
                "      body {\n" +
                "        background-color: #F5F5F5;\n" +
                "        margin: 0;\n" +
                "        padding: 0;    \n" +
                "\tfont-family: Arial, sans-serif;\n" +
                "\n" +
                "      }\n" +
                "      /* Styles pour le conteneur principal */\n" +
                "      .container {\n" +
                "        max-width: 600px;\n" +
                "        margin: 0 auto;\n" +
                "        background-color: #FFFFFF;\n" +
                "        padding: 20px;\n" +
                "        height: 100vh;\n" +
                "        display: flex;\n" +
                "        flex-direction: column;\n" +
                "        justify-content: center;\n" +
                "      }\n" +
                "      /* Styles pour le logo de l'entreprise */\n" +
                "      .logo {\n" +
                "        display: block;\n" +
                "        margin: -20px auto 20px;\n" +
                "        width: 100px;\n" +
                "        height: auto;\n" +
                "      }\n" +
                "      /* Styles pour le corps du texte */\n" +
                "      .text {\n" +
                "        text-align: center;\n" +
                "      }\n" +
                "      /* Styles pour le bouton animé */\n" +
                "      .button {\n" +
                "        display: inline-block;\n" +
                "        font-size: 16px;\n" +
                "        font-weight: bold;\n" +
                "        color: #3CAEA3;\n" +
                "        background-color: transparent;\n" +
                "        border-radius: 5px;\n" +
                "        padding: 10px 20px;\n" +
                "        border: 2px solid #3CAEA3;\n" +
                "        text-decoration: none;\n" +
                "        transition: all 0.5s ease;\n" +
                "      }\n" +
                "      .button:hover {\n" +
                "        background-color: #3CAEA3;\n" +
                "        color: #FFFFFF;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div class=\"container\">\n" +
                "      <img src=\"https://i.ibb.co/bHQGYdB/logo.jpg\" alt=\"Bienvenue\" padding-left=\"60%\" height=\"200px\" width=\"300px\">\n" +
                "<br>     \n" +
                " <div class=\"text\">\n" +
                "        <h1 style=\"color : #3CAEA3;\">Bonjour "+user.getFirstName()+" "+user.getLastName()+"</h1>\n" +
                "        <p>Vous avez demandez de réinitialiser votre mot de passe,</p>\n" +
                "        <p>ceci est votre email verification code:</p>\n" +
                "\n" +
                "<p style=\"color : red\">"+token+"</p>\n" +
                "       \n" +
                "\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>\n";
    }

    @Transactional
    public String requestResetPassword(String email)
    {

        User user = userRepo.findByEmail(email).get();
        if (user == null)
        {
            return "Pas de compte associé a cet e-mail";
        }
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                user);
        tokenService.saveConfirmationToken(confirmationToken);
        String link = "http://localhost:4200/mail-verif?token="+token;
        emailSender.send(user.getEmail(),buildEmailVerif(token, confirmationToken.getUser()));
        return "Vérification requise, un e-mail de verification a été envoyé!";
    }

    @Transactional
    public String passwordResetConfirm(String mailToken,String password)
    {
        ConfirmationToken confirmationToken = tokenService
                .getToken(mailToken).orElse(null);
        if (confirmationToken == null)
        {
            return "Veuillez vérifier le code envoyé par mail!";
        }



        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            String token2 = UUID.randomUUID().toString();

            ConfirmationToken confirmationToken2 = new ConfirmationToken(token2, LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(1),
                    confirmationToken.getUser());
            tokenService.saveConfirmationToken(confirmationToken2);
            //String link = "http://localhost:4200/mail-verif?token="+token2;
            emailSender.send(confirmationToken.getUser().getEmail(),token2);
            return "email expiré un nouvel e-mail a été envoyé!";
        }

        tokenService.setConfirmedAt(mailToken);



        User user = confirmationToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);
        return "Mot de passe modifée avec succes";
    }

    public User currentlyAuthenticatedUser()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).get();
    }
    public User currentlyAuthenticatedUser1() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            return userRepo.findByEmail(email).orElse(null);

    }




}

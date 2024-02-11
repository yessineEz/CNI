package com.example.stageback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String password ;
    @NotEmpty(message = "Le champ email ne peut pas être vide")
    @Email(message = "Veuillez saisir une adresse email valide")
    private String email;

    @NotEmpty(message = "Le champ nom ne peut pas être vide")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Le champ nom doit contenir uniquement des caractères alphabétiques")
    private String firstName;

    @NotEmpty(message = "Le champ prénom ne peut pas être vide")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Le champ prénom doit contenir uniquement des caractères alphabétiques")
    private String lastName;

}

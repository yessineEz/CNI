package com.example.stageback.servicesImplementation;

import com.example.stageback.entities.User;
import com.example.stageback.entities.demande;
import com.example.stageback.repositories.demandeRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service


public class demandeService {

    private final AuthenticationService authenticationService;
    private final demandeRepo demandeRep;

    // Injectez AuthenticationService et DemandeRepository via le constructeur
    public demandeService(AuthenticationService authenticationService, demandeRepo demandeRep) {
        this.authenticationService = authenticationService;
        this.demandeRep = demandeRep;
    }

    public demande ajouterDemande(demande demande) {
        // Vérifier si l'utilisateur actuellement authentifié a le rôle "USER"
        if (authenticationService.currentlyAuthenticatedUser().getRole().equals("USER")) {
            // Attribuer l'utilisateur actuellement authentifié à la demande
            demande.setUser(authenticationService.currentlyAuthenticatedUser());
            // Mettre à jour le statut de la demande et sa date
            demande.setStatusDemande("En cours de traitement par le modérateur");
            demande.setDateDemande(LocalDate.now());

            // Sauvegarder la demande dans la base de données
            demandeRep.save(demande);
        }

       else{
           System.err.println("Une demande ne peut etre ajoutée que par un simple utilisateur");
        }

        return demande;
    }

    public Optional<demande> modifierDemande(int id, demande demandeModifiee) {
        Optional<demande> demandeOptional = demandeRep.findById(id);
        if (demandeOptional.isPresent()) {
            demande demandeExistante = demandeOptional.get();
            // Mettre à jour les champs modifiables de la demande
            demandeExistante.setObjet(demandeModifiee.getObjet());
            demandeExistante.setStatusDemande(demandeModifiee.getStatusDemande());
            // Sauvegarder les modifications dans la base de données
            demandeRep.save(demandeExistante);
        }
        return demandeOptional;
    }

    public boolean supprimerDemande(int id) {
        if (demandeRep.existsById(id)) {
            demandeRep.deleteById(id);
            return true;
        }
        return false;
    }

    public List<demande> rechercherDemandeParType(String typeDemande) {
        return demandeRep.findByTypeDemande(typeDemande);
    }

    public List<demande> rechercherDemandeParStatut(String statutDemande) {
        return demandeRep.findByStatusDemande(statutDemande);
    }

    public void verifierDemande(int demandeId, boolean estAcceptee) {
        demande d = demandeRep.findById(demandeId).orElse(null);
        if (d != null) {
            User currentUser = authenticationService.currentlyAuthenticatedUser();
            if (currentUser.getRole().equals("MODERATEUR")) {
                if (estAcceptee) {
                    d.setStatusDemande("A traiter par l'admin");
                } else {
                    d.setStatusDemande("Rejetée");
                }
            } else if (currentUser.getRole().equals("ADMIN")) {
                d.setIsVerified(true);
            } else {
                d.setIsVerified(false);
            }
            demandeRep.save(d);
        }
    }

    public List<demande> afficherToutesLesDemandes() {
        return demandeRep.findAll();
    }

    public demande afficherDemandeParId(int id) {
        return demandeRep.findById(id).orElse(null);
    }

}


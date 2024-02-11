package com.example.stageback.controllers;

import com.example.stageback.entities.demande;
import com.example.stageback.servicesImplementation.demandeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/demande")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class demandeController {

    private final demandeService demandeServ;



    @PostMapping("/ajouter")
    public ResponseEntity<demande> ajouterDemande(@RequestBody demande d) {
        demande nouvelleDemande = demandeServ.ajouterDemande(d);
        return new ResponseEntity<>(nouvelleDemande, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/modifier")
    public ResponseEntity<demande> modifierDemande(@PathVariable int id, @RequestBody demande demandeModifiee) {
        Optional<demande> demandeModifieeOptional = demandeServ.modifierDemande(id, demandeModifiee);
        if (demandeModifieeOptional.isPresent()) {
            return new ResponseEntity<>(demandeModifieeOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<String> supprimerDemande(@PathVariable int id) {
        boolean suppressionReussie = demandeServ.supprimerDemande(id);
        if (suppressionReussie) {
            return new ResponseEntity<>("La demande a été supprimée avec succès", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("La demande avec l'ID spécifié n'existe pas", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/rechercher/type")
    public ResponseEntity<List<demande>> rechercherDemandeParType(@RequestParam String typeDemande) {
        List<demande> demandes = demandeServ.rechercherDemandeParType(typeDemande);
        if (!demandes.isEmpty()) {
            return new ResponseEntity<>(demandes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/rechercher/statut")
    public ResponseEntity<List<demande>> rechercherDemandeParStatut(@RequestParam String statutDemande) {
        List<demande> demandes = demandeServ.rechercherDemandeParStatut(statutDemande);
        if (!demandes.isEmpty()) {
            return new ResponseEntity<>(demandes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/verifier")
    public ResponseEntity<String> verifierDemande(@PathVariable int id, @RequestParam boolean estAcceptee) {
        demandeServ.verifierDemande(id, estAcceptee);
        return new ResponseEntity<>("Demande vérifiée avec succès", HttpStatus.OK);
    }

    @GetMapping("/toutes")
    public ResponseEntity<List<demande>> afficherToutesLesDemandes() {
        List<demande> demandes = demandeServ.afficherToutesLesDemandes();
        return new ResponseEntity<>(demandes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<demande> afficherDemandeParId(@PathVariable int id) {
        demande demande = demandeServ.afficherDemandeParId(id);
        if (demande != null) {
            return new ResponseEntity<>(demande, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

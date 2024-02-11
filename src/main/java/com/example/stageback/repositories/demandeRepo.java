package com.example.stageback.repositories;

import com.example.stageback.entities.demande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface demandeRepo extends JpaRepository<demande,Integer> {

    List<demande> findByTypeDemande(String typeDemande);
    List<demande> findByStatusDemande(String statutDemande);
}

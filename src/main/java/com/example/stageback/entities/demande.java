package com.example.stageback.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table( name = "Demandes")

public class demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String typeDemande;
    private int anneeDemande;
    private int numDemande;
    private LocalDate dateDemande;
    private String objet;
    private LocalDate dateSaisie;
    private String statusDemande;
    private String traceDemande;
    private Boolean isVerified;



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



}

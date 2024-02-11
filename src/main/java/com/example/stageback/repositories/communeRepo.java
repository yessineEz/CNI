package com.example.stageback.repositories;

import com.example.stageback.entities.ConfirmationToken;
import com.example.stageback.entities.commune;
import org.springframework.data.jpa.repository.JpaRepository;

public interface communeRepo extends JpaRepository<commune,Integer> {
}

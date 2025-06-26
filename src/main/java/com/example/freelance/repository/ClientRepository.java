package com.example.freelance.repository;

import com.example.freelance.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByNom(String nom);
    Client findByEmail(String email);

}

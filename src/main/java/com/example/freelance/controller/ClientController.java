package com.example.freelance.controller;

import com.example.freelance.model.Client;
import com.example.freelance.model.Mission;
import com.example.freelance.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public Client findClientByNom(String nom) {

        return clientService.findClientByNom(nom);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Client> findClientByEmail(String email) {

        Client client = clientService.findClientByEmail(email);
        if (client != null){
            return ResponseEntity.ok(client) ;
        }else {
          return ResponseEntity.notFound().build();
        }

    }


    @GetMapping("/{idClient}/mission")
    public List<Mission> findMissionByClientId(@PathVariable Long idClient){
        return clientService.findMissionByIdClient(idClient);
    }
}

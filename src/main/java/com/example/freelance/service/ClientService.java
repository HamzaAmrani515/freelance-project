package com.example.freelance.service;

import com.example.freelance.model.Client;
import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.ClientRepository;
import com.example.freelance.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final MissionRepository missionRepository;
    private final ClientRepository clientRepository;

    public Client findClientByNom(String nom){
        return clientRepository.findByNom(nom);
    }

    public List<Mission> findMissionByIdClient(Long idClient){
        return missionRepository.findTop50ByClientIdAndStatut(idClient, MissionStatut.EN_ATTENTE);
    }

}

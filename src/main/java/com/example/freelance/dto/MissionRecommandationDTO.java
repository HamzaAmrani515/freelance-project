package com.example.freelance.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MissionRecommandationDTO {

    private String titre;
    private String description;
    private Double budget;
    private String duree;

}

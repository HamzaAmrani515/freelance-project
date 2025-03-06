//package com.example.freelance;
//
//import com.example.freelance.service.MissionService;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
package com.example.freelance;

import com.example.freelance.service.MissionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FreelanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreelanceApplication.class, args);
	}
	@Bean
	CommandLineRunner insertMissions(MissionService missionService) {
		return args -> {
			missionService.createTestMissions();
			System.out.println("2 missions créées avec succès !");
		};
	}

}

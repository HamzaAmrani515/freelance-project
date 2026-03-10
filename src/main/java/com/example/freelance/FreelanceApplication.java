//package com.example.freelance;
//
//import com.example.freelance.service.MissionService;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
package com.example.freelance;
import com.example.freelance.repository.FreelancerReportRepository;
import com.example.freelance.repository.FreelancerReportRepository;
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
CommandLineRunner test(FreelancerReportRepository reportRepository) {
	return args -> {
		long count = reportRepository.count(); // Cela suffit à Hibernate pour forcer la reconnaissance
		System.out.println("Nombre de rapports existants : " + count);
	};
}

}

package ro.casabuna.casabuna_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class CasabunaBackendApplication {

	static void main(String[] args) {
		SpringApplication.run(CasabunaBackendApplication.class, args);
	}

}

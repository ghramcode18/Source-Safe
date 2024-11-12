package Geeks.Source.Safe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SourceSafeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SourceSafeApplication.class, args);
	}

}

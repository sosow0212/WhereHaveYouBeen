package hackathon.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WhereHaveYouBeenApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhereHaveYouBeenApplication.class, args);
    }

}

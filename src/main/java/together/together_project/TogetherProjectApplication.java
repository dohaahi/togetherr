package together.together_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TogetherProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TogetherProjectApplication.class, args);
    }

}

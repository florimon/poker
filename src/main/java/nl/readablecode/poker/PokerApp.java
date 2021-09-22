package nl.readablecode.poker;

import nl.readablecode.zkspring.ZkSpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ZkSpringConfiguration.class)
public class PokerApp {

    public static void main(String[] args) {
        SpringApplication.run(PokerApp.class, args);
    }
}

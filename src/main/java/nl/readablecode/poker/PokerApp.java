package nl.readablecode.poker;

import nl.readablecode.zkspring.ZkConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ZkConfiguration.class)
public class PokerApp {

    public static void main(String[] args) {
        SpringApplication.run(PokerApp.class, args);
    }
}

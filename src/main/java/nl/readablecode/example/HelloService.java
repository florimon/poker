package nl.readablecode.example;

import org.springframework.stereotype.Component;

@Component
public class HelloService {

    public String sayHello() {
        return "hello";
    }
}

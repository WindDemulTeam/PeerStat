package edu.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PeerPlatformApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(PeerPlatformApplication.class, args);
    }

}

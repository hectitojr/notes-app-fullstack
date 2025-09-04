package com.zoedatalab.notesbackend;

import com.zoedatalab.notesbackend.config.CorsProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CorsProps.class)
public class NotesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesBackendApplication.class, args);
    }

}

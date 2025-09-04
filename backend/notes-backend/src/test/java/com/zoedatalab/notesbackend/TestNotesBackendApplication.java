package com.zoedatalab.notesbackend;

import org.springframework.boot.SpringApplication;

public class TestNotesBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(NotesBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

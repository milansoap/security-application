package com.example.websecurity.controllers;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/greetings")
public class GreetingsController {

    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from our api");
    }

    @GetMapping("/say-good-bye")
    public ResponseEntity<String> sayGoodbye() {
        return ResponseEntity.ok("Goodbye and see you later");
    }



}

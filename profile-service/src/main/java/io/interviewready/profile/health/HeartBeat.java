package io.interviewready.profile.health;

import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeartBeat {
    @GetMapping("/health")
    public String healthCheck() {
        System.out.println("Got a health check!");
        return "";
    }
}

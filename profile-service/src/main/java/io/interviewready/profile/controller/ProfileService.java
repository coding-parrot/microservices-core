package io.interviewready.profile.controller;

import io.interviewready.profile.database.DBClient;
import io.interviewready.profile.models.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
public class ProfileService {
    private final DBClient databaseClient;

    @Autowired
    public ProfileService(final DBClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/profile")
    public Profile getProfile(@RequestParam final String id) {
        return databaseClient.getProfile(id);
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public void register(@RequestBody Profile profile) {
        databaseClient.addProfile(profile);
    }
}


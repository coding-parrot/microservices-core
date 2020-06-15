package io.interviewready.profile.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Profile {
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String imageUrl;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Profile(@JsonProperty("userId") final String userId,
                   @JsonProperty("firstName") final String firstName,
                   @JsonProperty("lastName") final String lastName,
                   @JsonProperty("password") final String password,
                   @JsonProperty("imageUrl") final String imageUrl) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

package io.interviewready.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.interviewready.profile.models.serviceclient.Registration;
import io.interviewready.profile.models.serviceclient.ServiceNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@SpringBootApplication
public class ProfileApplication {
    public static void main(String[] args) throws UnknownHostException, JsonProcessingException {
        final HttpClient client = HttpClient.newHttpClient();
        final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final Registration registration = new Registration("profile", new String[]{"profile", "register"});
        final String registrationJson = objectWriter.writeValueAsString(registration);
        final HttpRequest registerServiceRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:1234/service/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(registrationJson))
                .build();
        final ServiceNode profile = new ServiceNode(UUID.randomUUID().toString(), InetAddress.getLocalHost().getHostAddress(), "profile", 5001);
        final String profileJson = objectWriter.writeValueAsString(profile);
        System.out.println(profileJson);
        final HttpRequest registerNodeRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:1234/node"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(profileJson))
                .build();
        client.sendAsync(registerServiceRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .thenCompose(__ -> client.sendAsync(registerNodeRequest, HttpResponse.BodyHandlers.ofString()))
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
        SpringApplication.run(ProfileApplication.class, args);
    }
}

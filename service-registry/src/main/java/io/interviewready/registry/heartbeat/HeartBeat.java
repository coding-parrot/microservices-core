package io.interviewready.registry.heartbeat;

import io.interviewready.registry.database.DBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@Component
@EnableScheduling
public class HeartBeat {
    private final DBClient dbClient;
    private final HttpClient httpClient;
    private final Logger logger;

    @Autowired
    public HeartBeat(final DBClient dbClient) {
        this.dbClient = dbClient;
        this.httpClient = HttpClient.newHttpClient();
        logger = LoggerFactory.getLogger(HeartBeat.class.getCanonicalName());
    }

    @Scheduled(fixedRate = 7500)
    public void heartbeat() {
        dbClient.getAllServiceNodes()
                .thenApply(serviceNodes -> serviceNodes.stream()
                        .map(serviceNode -> {
                            final String url = "http://" + serviceNode.getIpAddress() + ":" + serviceNode.getPort() + "/health";
                            System.out.println(url);
                            final HttpRequest httpRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(url))
                                    .build();
                            return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                                    .orTimeout(1, TimeUnit.SECONDS)
                                    .thenApply(HttpResponse::statusCode)
                                    .whenComplete((statusCode, throwable) -> {
                                        if (throwable != null || statusCode != 200) {
                                            if (throwable != null) {
                                                logger.error("", throwable);
                                            } else {
                                                logger.error(serviceNode + " is UNRESPONSIVE!" + statusCode);
                                            }
                                            dbClient.removeNode(serviceNode.getId());
                                        } else {
                                            logger.info(serviceNode.getId() + " is alive.");
                                        }
                                    });
                        }).collect(Collectors.toList())).join();
    }
}

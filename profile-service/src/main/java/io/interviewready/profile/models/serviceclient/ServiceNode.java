package io.interviewready.profile.models.serviceclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceNode {
    private final String id;
    private final String ipAddress;
    private final String serviceName;
    private final int port;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ServiceNode(@JsonProperty("id") final String id,
                       @JsonProperty("ipAddress") final String ipAddress,
                       @JsonProperty("serviceName") final String serviceName,
                       @JsonProperty("port") final int port) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.serviceName = serviceName;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "ServiceNode{" +
                "id='" + id + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", port=" + port +
                '}';
    }
}

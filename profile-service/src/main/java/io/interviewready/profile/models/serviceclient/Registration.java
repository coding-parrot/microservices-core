package io.interviewready.profile.models.serviceclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Registration {

    private final String serviceName;
    private final String[] methodNames;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Registration(@JsonProperty("serviceName") final String serviceName,
                        @JsonProperty("methodNames") final String[] methodNames) {
        this.serviceName = serviceName;
        this.methodNames = methodNames;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String[] getMethodNames() {
        return methodNames;
    }
}

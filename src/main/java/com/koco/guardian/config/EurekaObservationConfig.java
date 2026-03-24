package com.koco.guardian.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.ClientRequestObservationContext;

import java.net.URI;
import java.util.Locale;

@Configuration
public class EurekaObservationConfig {

    @Bean
    public ObservationPredicate skipEurekaClientObservations() {
        return (name, context) -> !isEurekaClientCall(context);
    }

    private boolean isEurekaClientCall(Observation.Context context) {
        if (!(context instanceof ClientRequestObservationContext clientContext)) {
            return false;
        }

        if (clientContext.getCarrier() == null) {
            return false;
        }

        URI uri = clientContext.getCarrier().getURI();
        if (uri == null) {
            return false;
        }

        String normalizedUri = uri.toString().toLowerCase(Locale.ROOT);
        return normalizedUri.contains("/eureka/")
                || normalizedUri.endsWith("/eureka")
                || normalizedUri.contains(":8761/");
    }
}

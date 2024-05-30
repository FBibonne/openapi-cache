package fr.insee.oascache.metadata.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fr.insee.metadata.native.sparql")
public record SparqlConfiguration(String endpoint) {
}

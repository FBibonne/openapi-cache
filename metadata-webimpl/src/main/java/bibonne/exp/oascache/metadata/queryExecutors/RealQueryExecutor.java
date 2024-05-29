package bibonne.exp.oascache.metadata.queryExecutors;

import bibonne.exp.oascache.metadata.configuration.SparqlConfiguration;
import bibonne.exp.oascache.metadata.internal.QueryExecutor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Component
@Primary
@Slf4j
public record RealQueryExecutor(RestClient restClient, Function<String, URI> queryUri) implements QueryExecutor {

    @Autowired
    public RealQueryExecutor (SparqlConfiguration sparqlConfiguration){
        this(RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, "text/csv")
                .build(),
                query->URI.create(STR."\{sparqlConfiguration.endpoint()}?query=\{query}")
        );
    }

    public static final String PREFIXES =
            STR."""
                    PREFIX igeo: <http://rdf.insee.fr/def/geo#>
                    PREFIX dcterms: <http://purl.org/dc/terms/>
                    PREFIX xkos: <http://rdf-vocabulary.ddialliance.org/xkos#>
                    PREFIX evoc: <http://eurovoc.europa.eu/schema#>
                    PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
                    PREFIX dc: <http://purl.org/dc/elements/1.1/>
                    PREFIX insee: <http://rdf.insee.fr/def/base#>
                    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                    PREFIX pav: <http://purl.org/pav/>
                    PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
                    PREFIX prov: <http://www.w3.org/ns/prov#>
                    PREFIX sdmx-mm: <http://www.w3.org/ns/sdmx-mm#>
                    """;
    
    @Override
    public String execute(@NonNull String query) {
        String prefixedQuery = PREFIXES + query;
        String encodedQuery = this.encode(prefixedQuery);
        return restClient.get()
                .uri(queryUri.apply(encodedQuery))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (HttpRequest _, ClientHttpResponse response) -> {
                            log.error(
                                    STR."""
                                Encoded request in error : \{encodedQuery}
                                raw request in error : \{prefixedQuery}
                                """);
                            throw new RuntimeException(STR."Error \{response.getStatusText()} with message \{new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getBody().readAllBytes()))).lines().toList()}");
                        })
                .body(String.class);
    }

    private String encode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }
}

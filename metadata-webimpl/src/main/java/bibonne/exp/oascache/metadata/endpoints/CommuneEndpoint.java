package bibonne.exp.oascache.metadata.endpoints;

import bibonne.exp.oascache.metadata.api.GeoCommuneApi;
import bibonne.exp.oascache.metadata.api.model.Commune;
import bibonne.exp.oascache.metadata.internal.WebResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CommuneEndpoint implements GeoCommuneApi {
    private final WebResponseBuilder webResponseBuilder;

    @Override
    public ResponseEntity<Commune> getcogcom(String code, Optional<LocalDate> date) {
        return this.webResponseBuilder.forFindByCodeQuery(Commune.class, code, date).asOneObjet();
    }

    @Override
    public ResponseEntity<List<Commune>> getcogcomliste(Optional<LocalDate> date, Optional<String> filtreNom, Optional<Boolean> com) {
        return this.webResponseBuilder.forFindAllQuery(Commune.class, date).asList();
    }
}

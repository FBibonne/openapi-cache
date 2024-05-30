package fr.insee.oascache.metadata.endpoints;

import fr.insee.oascache.metadata.api.GeoDepartementApi;
import fr.insee.oascache.metadata.api.model.Departement;
import fr.insee.oascache.metadata.api.model.TerritoireRef;
import fr.insee.oascache.metadata.api.model.TypeEnum;
import fr.insee.oascache.metadata.internal.WebResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class DepartementEndpoints implements GeoDepartementApi {
    private final WebResponseBuilder webResponseBuilder;

    @Override
    public ResponseEntity<Departement> getcogdep(String code, Optional<LocalDate> date) throws Exception {
        return this.webResponseBuilder.forFindByCodeQuery(Departement.class, code, date).asOneObjet();
    }

    @Override
    public ResponseEntity<List<TerritoireRef>> getcogdepdesc(String code, Optional<LocalDate> date, Optional<String> filtreNom, Optional<TypeEnum> type) throws Exception {
        return this.webResponseBuilder.forFindDescQuery(Departement.class, code, date, TerritoireRef.class).asList();
    }
}

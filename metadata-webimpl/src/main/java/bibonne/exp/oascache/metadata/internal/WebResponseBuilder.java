package bibonne.exp.oascache.metadata.internal;

import bibonne.exp.oascache.metadata.internal.queries.FindAllQuery;
import bibonne.exp.oascache.metadata.internal.queries.FindByCodeQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public record WebResponseBuilder(QueryExecutor queryExecutor, Unmarshaller unmarshaller) {
    public <G> WebResponse<G> forFindByCodeQuery(Class<G> targetClass, String code, Optional<LocalDate> date) {
       return new WebResponse<>(this.queryExecutor.execute(FindByCodeQuery.INSTANCE.interpolate(targetClass.getSimpleName(), code, date)), targetClass, unmarshaller);
    }

    public <G> WebResponse<G> forFindAllQuery(Class<G> targetClass, Optional<LocalDate> date) {
        return new WebResponse<>(this.queryExecutor.execute(FindAllQuery.INSTANCE.interpolate(targetClass.getSimpleName(), date)), targetClass, unmarshaller);
    }

    public record WebResponse<G>(String queryResult, Class<G> targetClass, Unmarshaller unmarshaller) {
        public ResponseEntity<G> asOneObjet() {
            var entityToReturn=unmarshaller.unmarshal(queryResult, targetClass);
            return entityToReturn.map(ResponseEntity.ok()::body).orElse(ResponseEntity.notFound().build());
        }

        public ResponseEntity<List<G>> asList() {
            var entityToReturn=unmarshaller.unmarshalList(queryResult, targetClass);
            return ResponseEntity.ok().body(entityToReturn);
        }
    }
}

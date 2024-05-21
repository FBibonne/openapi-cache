package bibonne.exp.oascache.metadata.internal;

import bibonne.exp.oascache.metadata.api.model.Commune;
import jakarta.annotation.Nullable;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public interface Unmarshaller {
    <G> Optional<G> unmarshal(@NonNull String csv, @NonNull Class<G> targetClass);

    <G> List<G> unmarshalList(@NonNull String csv, @NonNull Class<G> targetClass);
}

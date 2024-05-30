package fr.insee.oascache.metadata.internal;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface Unmarshaller {
    <G> Optional<G> unmarshal(@NonNull String csv, @NonNull Class<G> targetClass);

    <G> List<G> unmarshalList(@NonNull String csv, @NonNull Class<G> targetClass);
}

package bibonne.exp.oascache.metadata.unmarshaller;

import bibonne.exp.oascache.metadata.api.model.Commune;
import bibonne.exp.oascache.metadata.api.model.Departement;
import bibonne.exp.oascache.metadata.internal.Unmarshaller;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
@RegisterReflectionForBinding(classes = {Commune.class, Departement.class})
public record JacksonUnmarshaller(CsvMapper csvMapper) implements Unmarshaller {

    public JacksonUnmarshaller() {
        this(CsvMapper.csvBuilder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .addModule(articleEnumModule())
                .addModule(new JavaTimeModule())
                .addModule(new JsonNullableModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build());
    }

    private static Module articleEnumModule() {
        var module = new SimpleModule();
        module.addDeserializer(Commune.TypeArticleEnum.class, new JsonDeserializer<>() {
            @Override
            public Commune.TypeArticleEnum deserialize(JsonParser parser, DeserializationContext ctxt) {
                try {
                    return Commune.TypeArticleEnum.values()[Integer.parseInt(parser.getValueAsString())];
                } catch (NumberFormatException | IOException e) {
                    return Commune.TypeArticleEnum._0_CHARNIERE_DE_;
                }
            }
        });
        return module;
    }

    @Override
    public <G> Optional<G> unmarshal(@NonNull String csv, @NonNull Class<G> targetClass) {
        return unmarshallAll(csv, targetClass, Optional.empty(), l-> l.isEmpty()?Optional.empty():Optional.of(l.getFirst()));
    }

    @Override
    public <G> List<G> unmarshalList(@NonNull String csv, @NonNull Class<G> targetClass) {
        return unmarshallAll(csv, targetClass, List.of(), Function.identity());
    }

    private <R, G> R unmarshallAll(String csv, Class<G> targetClass, R resultEmpty, Function<List<G>, R> extractResults){
        log.atDebug().log(() -> STR."""
            Deserialize for \{findReturned(targetClass, resultEmpty)} .
            CSV header is \{
                csv.lines().limit(1).findFirst().orElse(null)}
            """
        );
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader reader = csvMapper.readerFor(targetClass).with(schema);
        List<G> results;
        try (MappingIterator<G> mappingIterator = reader.readValues(csv)) {
            results = mappingIterator.readAll();
        } catch (IOException e) {
            log.error(STR."""
            While reading
            \{csv}
            MESSAGE : \{e.getMessage()}
            ===> RETURN WILL BE EMPTY
            """);
            return resultEmpty;
        }
        return extractResults.apply(results);
    }

    private <G, R> String findReturned(Class<G> targetClass, R resultEmpty) {
        return switch (resultEmpty){
            case List<?> _ -> STR."List<\{targetClass.getName()}>";
            case Optional<?> _ -> targetClass.getName();
            default -> STR."Unknown wrapper for \{targetClass.getName()}";
        };
    }
}

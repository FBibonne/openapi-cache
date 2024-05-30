package fr.insee.oascache.metadata.utils;

import fr.insee.oascache.metadata.annotations.HttpCache;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;

public interface CacheAndDuration {

    static HttpCache findCacheAnnotation(@NonNull Method method) {
        return Objects.requireNonNull(method.getAnnotation(HttpCache.class));
    }

    static Duration computeDuration(@NonNull Method method){
        return computeDuration(findCacheAnnotation(method));
    }

    private static Duration computeDuration(HttpCache httpCache) {
        return Duration.of(httpCache.duration(), httpCache.unit().toChronoUnit());
    }
}

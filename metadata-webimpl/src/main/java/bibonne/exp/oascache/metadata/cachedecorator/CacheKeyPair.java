package bibonne.exp.oascache.metadata.cachedecorator;

import lombok.NonNull;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static bibonne.exp.oascache.metadata.utils.CacheAndDuration.computeDuration;

public record CacheKeyPair(Method method, List<Object> arguments) {
    public CacheKeyPair(@NonNull Method method, Object[] arguments){
        this(method, arguments==null?List.of(): Arrays.asList(arguments));
    }

    public Duration getDuration() {
        return computeDuration(method);
    }
}

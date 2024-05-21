package bibonne.exp.oascache.metadata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

/**
 * This annotation is supposed to be used for endpoint methods whose ressource is intended to be cached by Http clients.
 *
 * For example, if an endpoint method controller is annotated with `@HttpCache(duration=30, unit=TimeUnit.DAYS)`, the
 * http response of the endpoint controller method should indicate to cache it for 30 days, so it should contain the header
 * `Cache-Control: max-age=2592000`
 *
 * Default values ( `@HttpCache()` ) imply a cache of one hour : Cache-Control: max-age=3600`
 */
public @interface HttpCache {
    /**
     * The value of the duration for the cache. Default to 1
     * @return an int which indicates the duration of the cache
     */
    int duration() default 1;

    /**
     * The unit of the duration for the cache. Default to hours
     * @return an TimeUnit which indicates the unit of the duration of the cache
     */
    TimeUnit unit() default TimeUnit.HOURS;
}

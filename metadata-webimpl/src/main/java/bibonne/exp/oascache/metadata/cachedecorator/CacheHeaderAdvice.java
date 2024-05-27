package bibonne.exp.oascache.metadata.cachedecorator;

import bibonne.exp.oascache.metadata.annotations.HttpCache;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

@Component
public class CacheHeaderAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        HttpCache httpCache = findCacheAnnotation(method);
        var responseFromCache = retrieveFromCache(method, invocation.getArguments());
        Object response = responseFromCache.isPresent()?responseFromCache.get():invokeThenCache(invocation);
        return responseWithCacheHeaders(response, httpCache);
    }

    private Object responseWithCacheHeaders(Object response, HttpCache httpCache) {
        if (response instanceof ResponseEntity<?> responseEntity) {
            var newResponseEntity = ResponseEntity.status(responseEntity.getStatusCode());
            newResponseEntity.headers(responseEntity.getHeaders());
            newResponseEntity.cacheControl(cacheControl(httpCache));
            newResponseEntity.body(responseEntity.getBody());
            response=newResponseEntity.build();
        }
        return response;
    }

    private CacheControl cacheControl(HttpCache httpCache) {
        var duration = httpCache.duration();
        return duration <= 0 ?
                CacheControl.noCache():
                CacheControl.maxAge(duration, httpCache.unit()).mustRevalidate();
    }

    private static HttpCache findCacheAnnotation(Method method) {
        return Objects.requireNonNull(method.getAnnotation(HttpCache.class));
    }

    private static Object invokeThenCache(MethodInvocation invocation) throws Throwable {
        Object response;
        response = invocation.proceed();
        putInCache(invocation.getMethod(), invocation.getArguments(), response);
        return response;
    }

    private static void putInCache(Method method, Object[] arguments, Object response) {
        //TODO Implement it : find the duration, get the cache with right TTL, compute key then put response with the key
    }

    private Optional<?> retrieveFromCache(Method method, Object[] arguments) {
        //TODO Implement it : find the duration, get the cache with right TTL, compute key then get the response
        return Optional.empty();
    }
}

package fr.insee.oascache.metadata.cachedecorator;

import fr.insee.oascache.metadata.annotations.HttpCache;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.lang.reflect.Method;
import java.util.Optional;

import static fr.insee.oascache.metadata.cachedecorator.CacheAOPConfig.CACHE_NAME;
import static fr.insee.oascache.metadata.utils.CacheAndDuration.findCacheAnnotation;

@Component
public class CacheHeaderAdvice implements MethodInterceptor {

    private final Cache<CacheKeyPair, Object> cache;

    public CacheHeaderAdvice(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(CACHE_NAME);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        HttpCache httpCache = findCacheAnnotation(method);
        var responseFromCache = retrieveFromCache(method, invocation.getArguments());
        Object response = responseFromCache.isPresent() ? responseFromCache.get() : invokeThenCache(invocation);
        return responseWithCacheHeaders(response, httpCache);
    }

    private Object responseWithCacheHeaders(Object response, HttpCache httpCache) {
        if (response instanceof ResponseEntity<?> responseEntity) {
            response = ResponseEntity.status(responseEntity.getStatusCode())
                    .headers(responseEntity.getHeaders())
                    .cacheControl(cacheControl(httpCache))
                    .body(responseEntity.getBody());
        }
        return response;
    }

    private CacheControl cacheControl(HttpCache httpCache) {
        var duration = httpCache.duration();
        return duration <= 0 ?
                CacheControl.noCache() :
                CacheControl.maxAge(duration, httpCache.unit()).mustRevalidate();
    }

    private Object invokeThenCache(MethodInvocation invocation) throws Throwable {
        Object response;
        response = invocation.proceed();
        putInCache(invocation.getMethod(), invocation.getArguments(), response);
        return response;
    }

    private void putInCache(Method method, Object[] arguments, Object response) {
        cache.put(new CacheKeyPair(method, arguments), response);
    }

    private Optional<?> retrieveFromCache(Method method, Object[] arguments) {
        return Optional.ofNullable(cache.get(new CacheKeyPair(method, arguments)));
    }

}

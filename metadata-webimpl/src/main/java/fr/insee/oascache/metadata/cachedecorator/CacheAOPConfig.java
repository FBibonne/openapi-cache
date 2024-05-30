package fr.insee.oascache.metadata.cachedecorator;

import fr.insee.oascache.metadata.annotations.HttpCache;
import lombok.NonNull;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.Pointcuts;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.function.Supplier;

@Configuration
public class CacheAOPConfig {

    public static final String CACHE_NAME = "geocache";

    @Bean
    static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    Advisor aopAdvisor(CacheHeaderAdvice cacheHeaderAdvice){
        return new DefaultPointcutAdvisor(pointcutForEndpointCache(), cacheHeaderAdvice);
    }

    private Pointcut pointcutForEndpointCache() {
        return Pointcuts.intersection(
                AnnotationMatchingPointcut.forClassAnnotation(Controller.class),
                AnnotationMatchingPointcut.forMethodAnnotation(HttpCache.class)
        );
    }


    @Bean
    public CacheManager cacheManager() throws IOException {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        EhcacheCachingProvider ehcacheProvider = (EhcacheCachingProvider) cachingProvider;
        DefaultConfiguration configuration = new DefaultConfiguration(ehcacheProvider.getDefaultClassLoader(),
                new DefaultPersistenceConfiguration(Files.createTempDirectory(CACHE_NAME).toFile()));
        configuration.addCacheConfiguration(CACHE_NAME,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(CacheKeyPair.class, Object.class, ResourcePoolsBuilder.heap(10))
                        .withExpiry(new SpecialExpiryPolicy())
                        .build()
        );
        return ehcacheProvider.getCacheManager(ehcacheProvider.getDefaultURI(), configuration);
    }

    private record SpecialExpiryPolicy() implements ExpiryPolicy<CacheKeyPair, Object> {

        @Override
        public Duration getExpiryForCreation(@NonNull CacheKeyPair key, Object value) {
            return key.getDuration();
        }

        @Override
        public Duration getExpiryForAccess(CacheKeyPair key, Supplier<?> value) {
            return null;
        }

        @Override
        public Duration getExpiryForUpdate(CacheKeyPair key, Supplier<?> oldValue, Object newValue) {
            return getExpiryForCreation(key,newValue);
        }
    }
}

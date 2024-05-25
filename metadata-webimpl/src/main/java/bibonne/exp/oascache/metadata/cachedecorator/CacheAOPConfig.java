package bibonne.exp.oascache.metadata.cachedecorator;

import bibonne.exp.oascache.metadata.annotations.HttpCache;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.Pointcuts;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

@Configuration
public class CacheAOPConfig {

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

}

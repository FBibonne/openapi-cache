package bibonne.exp.oascache.metadata.cachedecorator;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class CacheHeaderAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
        if (returnValue instanceof ResponseEntity<?> responseEntity){
            HttpHeaders headers = responseEntity.getHeaders();
        }
    }
}

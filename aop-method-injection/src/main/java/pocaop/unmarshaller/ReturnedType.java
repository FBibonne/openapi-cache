package pocaop.unmarshaller;

import lombok.NonNull;
import org.springframework.core.ResolvableType;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.Collection;

public record ReturnedType(@NonNull ResolvableType resolvableType) {

    public ReturnedType{
        if (resolvableType.resolve()!=null && ResponseEntity.class.isAssignableFrom(resolvableType.resolve())){
            resolvableType=resolvableType.getGeneric();
        }
    }

    public ReturnedType(@NonNull Method calledMethod){
        this(ResolvableType.forMethodReturnType(calledMethod));
    }

    public Class<?> typeForMapping() {
        return resolvableType.hasGenerics()?resolvableType.getGeneric().resolve():resolvableType.resolve();
    }

    public boolean isCollection() {
        return resolvableType.resolve()!=null && Collection.class.isAssignableFrom(resolvableType.resolve());
    }
}

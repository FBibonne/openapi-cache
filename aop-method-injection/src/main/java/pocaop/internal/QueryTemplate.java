package pocaop.internal;

import lombok.NonNull;
import pocaop.internal.exceptions.ArgumentException;
import pocaop.internal.exceptions.ArgumentUnvailableException;
import pocaop.internal.exceptions.BadArgumentTypeException;
import pocaop.internal.providers.CodeProvider;
import pocaop.internal.providers.DateProvider;
import pocaop.internal.providers.TerritoireProvider;
import pocaop.internal.queries.FindAllQuery;
import pocaop.internal.queries.FindByCodeQuery;
import pocaop.internal.queries.FindDescQuery;

import java.time.LocalDate;
import java.util.Optional;

public interface QueryTemplate{

     String format(Object[] arguments) throws ArgumentException;

    static QueryTemplate ofFindByCode(@NonNull FindByCodeQuery findByCodeQuery, @NonNull TerritoireProvider territoireProvider,
                                      @NonNull CodeProvider codeProvider, @NonNull DateProvider dateProvider){
        return arguments -> findByCodeQuery.interpolate(territoireProvider.provide(arguments), codeProvider.provide(arguments), dateProvider.provide(arguments));
    }

    static QueryTemplate ofFindAll(@NonNull FindAllQuery findAllQuery, @NonNull TerritoireProvider territoireProvider, @NonNull DateProvider dateProvider){
        return arguments -> findAllQuery.interpolate(territoireProvider.provide(arguments), dateProvider.provide(arguments));
    }

    static QueryTemplate ofFindDesc(@NonNull FindDescQuery findDescQuery,@NonNull CodeProvider codeProvider, @NonNull DateProvider dateProvider){
        return arguments -> findDescQuery.interpolate(codeProvider.provide(arguments), dateProvider.provide(arguments));
    }

    static String resolveAsString(@NonNull Object[] arguments, int i) throws ArgumentException {
        if (i >= arguments.length) {
            throw new ArgumentUnvailableException("String", i);
        }
        Object argument = arguments[i];
        if (!(argument instanceof String)) {
            throw new BadArgumentTypeException("String", argument, i);
        }
        return (String) argument;
    }

    static Optional<LocalDate> resolveAsOptionalDate(@NonNull Object[] arguments, int i) throws ArgumentException {
        if (i >= arguments.length) {
            throw new ArgumentUnvailableException("Optional<LocalDate>", i);
        }
        Object argument = arguments[i];
        if (!(argument instanceof Optional<?> optionalArgument)) {
            throw new BadArgumentTypeException("Optional<LocalDate>", argument, i);
        }
        if (optionalArgument.isPresent() && ! (optionalArgument.get() instanceof LocalDate)) {
            throw new BadArgumentTypeException("Optional<LocalDate>", argument, i);
        }
        return optionalArgument.map(LocalDate.class::cast);
    }



}

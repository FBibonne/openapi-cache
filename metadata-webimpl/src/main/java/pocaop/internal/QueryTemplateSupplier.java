package pocaop.internal;

import lombok.NonNull;
import pocaop.internal.providers.CodeProvider;
import pocaop.internal.providers.DateProvider;
import pocaop.internal.queries.FindAllQuery;
import pocaop.internal.queries.FindByCodeQuery;
import pocaop.internal.queries.FindDescQuery;

import java.util.Optional;

public class QueryTemplateSupplier {

    public Optional<QueryTemplate> get(@NonNull String methodName) {
        return Optional.ofNullable(switch (methodName) {
            case "getcogcom" -> queryTemplate().withFindByCode()
                    .codeAtPosition(0)
                    .dateAtPosition(1)
                    .forTerritory("Commune");
            case "getcogdep" -> queryTemplate().withFindByCode()
                    .codeAtPosition(0)
                    .dateAtPosition(1)
                    .forTerritory("Departement");
            case "getTerritoireByTypeAndCode" -> queryTemplate().withFindByCode()
                    .codeAtPosition(0)
                    .dateAtPosition(2)
                    .forUnknownTerritoryType(1);
            case "getcogcomliste" -> queryTemplate().withFindAll()
                    .dateAtPosition(0)
                    .forTerritory("Commune");
            case "getcogdepdesc" -> queryTemplate().withFindDesc()
                    .codeAtPosition(0)
                    .dateAtPosition(1)
                    .forTerritory("Departement");
            default -> null;
        });
    }

    private QueryTemplateBuilder queryTemplate() {
        return new QueryTemplateBuilder();
    }

    private sealed interface QueryWrapperProvider{
    }

    private record QueryTemplateBuilder() {
        public QueryTemplateBuilderWithRequest withFindByCode() {
            return new QueryTemplateBuilderWithRequest(new FindByCodeProvider(), null, null);
        }

        public QueryTemplateBuilderWithRequest withFindAll() {
            return new QueryTemplateBuilderWithRequest(new AllQueryProvider(), null, null);
        }

        public QueryTemplateBuilderWithRequest withFindDesc() {
            return new QueryTemplateBuilderWithRequest(new FindDescProvider(), null, null);
        }

        private record FindByCodeProvider() implements QueryWrapperProvider {
        }

        private record AllQueryProvider() implements QueryWrapperProvider {
        }

        private record FindDescProvider() implements QueryWrapperProvider {
        }
    }

    private record QueryTemplateBuilderWithRequest(QueryWrapperProvider queryWrapperProvider,
                                                   CodeProvider codeProvider,
                                                   DateProvider dateProvider) {
        public QueryTemplate forTerritory(String territoire) {
            return switch (queryWrapperProvider){
                case QueryTemplateBuilder.AllQueryProvider _
                        -> QueryTemplate.ofFindAll(FindAllQuery.INSTANCE, _ -> territoire, dateProvider);
                case QueryTemplateBuilder.FindByCodeProvider _
                        -> QueryTemplate.ofFindByCode(FindByCodeQuery.INSTANCE, _ -> territoire, codeProvider, dateProvider);
                case QueryTemplateBuilder.FindDescProvider _
                        -> QueryTemplate.ofFindDesc(queryWrapperForFindByDesc(territoire), codeProvider, dateProvider);
                };
        }

        public QueryTemplate forUnknownTerritoryType(int position) {
            return switch (queryWrapperProvider){
                case QueryTemplateBuilder.AllQueryProvider _
                        -> QueryTemplate.ofFindAll(FindAllQuery.INSTANCE, args -> QueryTemplate.resolveAsString(args, position), dateProvider);
                case QueryTemplateBuilder.FindByCodeProvider _
                        -> QueryTemplate.ofFindByCode(FindByCodeQuery.INSTANCE, args -> QueryTemplate.resolveAsString(args, position), codeProvider, dateProvider);
                case QueryTemplateBuilder.FindDescProvider _
                        -> QueryTemplate.ofFindDesc(queryWrapperForFindByDesc(null), codeProvider, dateProvider);
            };
        }

        private static FindDescQuery queryWrapperForFindByDesc(String territoire) {
            if (territoire ==null){
                throw new UnsupportedOperationException("Impossible to provide FindDescQuery when territoire is null (ie it is impossible to find all desc territories of a unknown type of territory");
            }
            return new FindDescQuery(territoire);
        }

        public QueryTemplateBuilderWithRequest codeAtPosition(int i) {
            return new QueryTemplateBuilderWithRequest(this.queryWrapperProvider, args -> QueryTemplate.resolveAsString(args, i), this.dateProvider);
        }

        public QueryTemplateBuilderWithRequest dateAtPosition(int i) {
            return new QueryTemplateBuilderWithRequest(this.queryWrapperProvider, this.codeProvider, args -> QueryTemplate.resolveAsOptionalDate(args, i));
        }
    }
}

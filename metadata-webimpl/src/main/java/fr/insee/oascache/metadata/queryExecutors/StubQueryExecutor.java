package fr.insee.oascache.metadata.queryExecutors;

import fr.insee.oascache.metadata.internal.QueryExecutor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public record StubQueryExecutor() implements QueryExecutor {

    private static final String CSV_2_ROWS = """
            "uri","code","territoireType","typeArticle","intitule","intituleSansArticle","dateCreation","dateSuppression","chefLieu","categorieJuridique","intituleComplet"
            "http://id.insee.fr/geo/commune/72d08f78-9b5c-46c6-bdaf-b31ed46216a0","33529","Commune","3","La Teste","Teste","1943-01-01","1994-06-13",,,
            "http://id.insee.fr/geo/commune/34e3a788-bc74-4334-a764-209b5b45b3fc","33529","Commune","3","La Teste-de-Buch","Teste-de-Buch","1994-06-13",,,,
            """;
    private static final String CSV_EMPTY = """
            "uri","code","territoireType","typeArticle","intitule","intituleSansArticle","dateCreation","dateSuppression","chefLieu","categorieJuridique","intituleComplet"
            """;
    private static final String CSV_DEP = """
            "uri","code","territoireType","typeArticle","intitule","intituleSansArticle","dateCreation","dateSuppression","chefLieu","categorieJuridique","intituleComplet"
            "http://id.insee.fr/geo/departement/bee9c7ed-221a-46be-9e42-8b815ce285c4","33","Departement","3","Gironde",Gironde",,,"Bordeaux",,
            """;

    @Override
    public String execute(@NonNull String query) {
       if (!(query.contains("igeo:Commune")) && !(query.contains("igeo:Departement"))){
           throw new RuntimeException("Emulated database error");
       }
       if(query.contains("33529")){
           return CSV_2_ROWS;
       }
        if(query.contains("33")){
            return CSV_DEP;
        }
        return CSV_EMPTY;
    }
}

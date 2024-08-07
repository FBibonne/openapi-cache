package fr.insee.oascache.metadata;

import fr.insee.oascache.metadata.api.model.Commune;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GeoCommuneClientTest {

    @Autowired
    private GeoCommuneClient geoCommuneClient;

    @Test
    void test(){
        Commune commune = geoCommuneClient.getcogcom("33529", (LocalDate) null);
        assertThat(commune.getIntitule()).hasToString("La Teste");
    }

}

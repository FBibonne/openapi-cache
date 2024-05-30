package fr.insee.oascache.metadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.time.LocalDate;

@SpringBootApplication
@EnableFeignClients
public class TestFeign {
/*
    - Test
*/

    public static void main(String[] args) {
        var context=(new SpringApplication(TestFeign.class)).run(args);
        GeoCommuneClient  client = context.getBean(GeoCommuneClient.class);
        client.getcogcom("33529", (LocalDate) null);
    }




}

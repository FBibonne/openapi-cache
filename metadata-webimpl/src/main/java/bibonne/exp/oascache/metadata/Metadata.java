package bibonne.exp.oascache.metadata;

import bibonne.exp.oascache.metadata.configuration.PropertiesLogger;
import bibonne.exp.oascache.metadata.configuration.SparqlConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Metadata {

    public static void main(String[] args) {
        configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);
    }

    private static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder) {
        return springApplicationBuilder.sources(Metadata.class)
                .listeners(new PropertiesLogger());
    }

}


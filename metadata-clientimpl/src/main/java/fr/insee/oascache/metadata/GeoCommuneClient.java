package fr.insee.oascache.metadata;

import fr.insee.oascache.metadata.client.GeoCommuneApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "metadata", configuration = MetadataFeignClientConfiguration.class)
public interface GeoCommuneClient extends GeoCommuneApi {
}

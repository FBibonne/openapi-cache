package poclient;

import bibonne.exp.oascache.metadata.client.GeoCommuneApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "metadata", configuration = MetadataFeignClientConfiguration.class)
public interface GeoCommuneClient extends GeoCommuneApi {
}

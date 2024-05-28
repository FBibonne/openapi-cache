package poclient;

import feign.Contract;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MetadataFeignClientConfiguration {

    @Bean
    public Contract feignContract(){
        return new Contract.Default();
    }

    @Bean
    public feign.okhttp.OkHttpClient client() throws IOException {
        int cacheSize = 10 * 1024 * 1024;

        File cacheDirectory = Files.createTempDirectory("cache").toFile();
        Cache cache = new Cache(cacheDirectory, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        return new feign.okhttp.OkHttpClient(client);
    }

}

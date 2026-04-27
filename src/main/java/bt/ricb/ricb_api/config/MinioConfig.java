package bt.ricb.ricb_api.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://192.168.0.103:9000")
                .credentials("adminuser", "z2EL4fjGTDT88rGawlhHHC8Q")
                .build();
    }
}
package bt.ricb.ricb_api.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://192.168.123.145:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }
}
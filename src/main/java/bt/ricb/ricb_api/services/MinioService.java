package bt.ricb.ricb_api.services;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName = "ricb";

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadFile(MultipartFile file) throws Exception {

        // ✅ Year folder
        String year = String.valueOf(LocalDate.now().getYear());

        // ✅ Original file name parts
        String originalName = file.getOriginalFilename();

        String name = "";
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            name = originalName.substring(0, originalName.lastIndexOf("."));
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // ✅ Timestamp (readable format)
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // ✅ Final file name
        String newFileName = name + "_" + timestamp + extension;

        // ✅ MinIO object path
        String objectName = year + "/" + newFileName;

        // ✅ Upload to MinIO
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1L)
                        .contentType(file.getContentType())
                        .build()
        );

        return objectName;
    }
}
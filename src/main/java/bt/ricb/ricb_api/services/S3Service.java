package bt.ricb.ricb_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.ResponseInputStream;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucketName;

    // Upload file with auto-generated key
    public String uploadFile(String originalFileName, InputStream inputStream, long size) {
        try {
            String key = "claims/" + java.util.UUID.randomUUID() + "_" + originalFileName;
            return uploadFileWithCustomName(key, inputStream, size);
        } catch (Exception e) {
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }

    // Upload file with a custom key (folder + filename)
    public String uploadFileWithCustomName(String key, InputStream inputStream, long size) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(size)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(inputStream, size)
            );

            return "https://" + bucketName + ".s3.amazonaws.com/" + key;
        } catch (Exception e) {
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }

    // Download file from S3
    public byte[] downloadFile(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = s3Object.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                return baos.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3: " + e.getMessage(), e);
        }
    }
    // ================= Delete file from S3 =================
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }}
}
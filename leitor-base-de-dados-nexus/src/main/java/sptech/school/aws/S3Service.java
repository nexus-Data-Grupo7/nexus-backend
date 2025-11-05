// src/main/java/school/sptech/aws/S3Service.java
package sptech.school.aws;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class S3Service {

    private final S3Client s3Client;

    public S3Service() {
        this.s3Client = S3Provider.getClient();
    }

    public InputStream getFileAsInputStream(String bucketName, String fileKey) {
        System.out.printf("Iniciando download do arquivo '%s' do bucket '%s'...%n", fileKey, bucketName);
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            return s3Client.getObject(getObjectRequest);
        } catch (S3Exception e) {
            System.err.println("Erro ao buscar arquivo no S3: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Falha ao buscar arquivo no S3.", e);
        }
    }

    public List<String> listObjects(String bucketName, String prefix) {
        try {
            ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();

            List<S3Object> objects = s3Client.listObjectsV2(listReq).contents();

            java.util.ArrayList<String> keys = new java.util.ArrayList<>();
            for (S3Object obj : objects) {
                keys.add(obj.key());
            }

            return keys;
        } catch (S3Exception e) {
            System.err.println("Erro ao listar objetos no S3: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Falha ao listar objetos do S3", e);
        }
    }
}
package com_abertamente_cms.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "gcp")
public class GcpFileStorageServiceImpl implements FileStorageService {

    @Value("${gcp.project.id}")
    private String projectId;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    private Storage storage;

    @PostConstruct
    public void init() throws IOException {
        String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IllegalArgumentException("A variável de ambiente GOOGLE_APPLICATION_CREDENTIALS não está configurada.");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build()
                .getService();
    }

    @Override
    public String storeFile(MultipartFile file, String directory) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String fullPath = directory + "/" + fileName;

            BlobId blobId = BlobId.of(bucketName, fullPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            // A URL pública padrão do Google Cloud Storage
            return "https://storage.googleapis.com/" + bucketName + "/" + fullPath;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao fazer upload do arquivo para o Google Cloud Storage", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            // filePath vem como URL: https://storage.googleapis.com/bucketName/posts/arquivo.jpg
            String prefix = "https://storage.googleapis.com/" + bucketName + "/";
            if (filePath != null && filePath.startsWith(prefix)) {
                String objectName = filePath.substring(prefix.length());
                BlobId blobId = BlobId.of(bucketName, objectName);
                storage.delete(blobId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao excluir o arquivo do Google Cloud Storage: " + filePath, e);
        }
    }
}

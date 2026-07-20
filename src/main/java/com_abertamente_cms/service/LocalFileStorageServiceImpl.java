package com_abertamente_cms.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation = Paths.get("storage/uploads");

    public LocalFileStorageServiceImpl() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar o diretório de storage", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String directory) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }

        String newFilename = UUID.randomUUID().toString() + extension;
        Path targetDir = rootLocation.resolve(directory);

        try {
            Files.createDirectories(targetDir);
            Path targetLocation = targetDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + directory + "/" + newFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao salvar o arquivo " + newFilename, ex);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        // Exemplo: "/uploads/avatars/abc.jpg" -> "storage/uploads/avatars/abc.jpg"
        if (filePath != null && filePath.startsWith("/uploads/")) {
            String relativePath = filePath.substring("/uploads/".length());
            Path targetLocation = rootLocation.resolve(relativePath);
            try {
                Files.deleteIfExists(targetLocation);
            } catch (IOException e) {
                System.err.println("Erro ao deletar arquivo local: " + e.getMessage());
            }
        }
    }
}

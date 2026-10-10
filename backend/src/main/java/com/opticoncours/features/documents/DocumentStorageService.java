package com.opticoncours.features.documents;

import com.opticoncours.common.exception.BadRequestException;
import com.opticoncours.common.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class DocumentStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path fileStorageLocation;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Répertoire de stockage des pièces justificatives initialisé : {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible d'initialiser le répertoire de stockage des documents téléversés.", ex);
        }
    }

    public String storeFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le document transmis est vide.");
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = getFileExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Format de document non autorisé. Formats acceptés : PDF, JPG, PNG.");
        }

        String fileName = prefix + "_" + UUID.randomUUID() + "." + extension;

        try {
            if (fileName.contains("..")) {
                throw new BadRequestException("Nom de fichier invalide.");
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/api/files/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Échec de la sauvegarde du fichier " + fileName, ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Document introuvable : " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Document introuvable : " + fileName);
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) return "";
        return fileName.substring(lastIndex + 1);
    }
}

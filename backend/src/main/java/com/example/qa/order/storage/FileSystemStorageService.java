package com.example.qa.order.storage;

import com.example.qa.errorhandling.ApiException;
import com.example.qa.order.OrderService;
import com.example.qa.order.model.Attachment;
import com.example.qa.order.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService{

    private final Path rootLocation;
    private static HashMap<UUID, String> uuidStringHashMap = new HashMap<>();

    @Autowired
    public FileSystemStorageService(StorageProperties properties, OrderService orderService){
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file, UUID uuid) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            uuidStringHashMap.put(uuid, file.getOriginalFilename());
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(uuid.toString()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(UUID uuid) {
        return rootLocation.resolve(uuid.toString());
    }

    @Override
    public Resource loadAsResource(UUID uuid) {
        try {
            Path file = load(uuid);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + uuid);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + uuid, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void delete(UUID uuid) {
        Path destinationFile = this.rootLocation.resolve(
                        Paths.get(uuid.toString()))
                .normalize().toAbsolutePath();
        try {
            FileSystemUtils.deleteRecursively(destinationFile);
        }catch (Exception exception){
            throw new StorageFileNotFoundException("Could not find file" + uuid, exception);
        }
    }

    @Override
    public String getNameByUUID(UUID uuid) {
        return uuidStringHashMap.get(uuid);
    }
}

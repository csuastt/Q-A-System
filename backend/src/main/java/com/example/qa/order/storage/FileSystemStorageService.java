package com.example.qa.order.storage;

import com.example.qa.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private static HashMap<UUID, String> uuidStringHashMap = new HashMap<>();
    private static HashMap<UUID, String> uuidStringHashMapPic = new HashMap<>();
    private final Path rootLocation;
    private final Path rootLocationPic;

    @Autowired
    public FileSystemStorageService(StorageProperties properties, OrderService orderService) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.rootLocationPic = Paths.get(properties.getLocationPic());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(rootLocationPic);
        } catch (IOException e) {
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
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public void storePic(MultipartFile file, UUID uuid) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty pic.");
            }
            uuidStringHashMapPic.put(uuid, file.getOriginalFilename());
            Path destinationFile = this.rootLocationPic.resolve(
                            Paths.get(uuid.toString()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocationPic.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store pic.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(UUID uuid) {
        return rootLocation.resolve(uuid.toString());
    }

    @Override
    public Path loadPic(UUID uuid) {
        return rootLocationPic.resolve(uuid.toString());
    }

    @Override
    public Resource loadAsResource(UUID uuid) {
        try {
            Path file = load(uuid);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + uuid);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + uuid, e);
        }
    }

    @Override
    public Resource loadAsResourcePic(UUID uuid) {
        try {
            Path file = loadPic(uuid);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read pic: " + uuid);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read pic: " + uuid, e);
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
        } catch (Exception exception) {
            throw new StorageFileNotFoundException("Could not find file" + uuid, exception);
        }
    }

    @Override
    public String getNameByUUID(UUID uuid) {
        return uuidStringHashMap.get(uuid);
    }

    @Override
    public String getNameByUUIDPic(UUID uuid) {
        return uuidStringHashMapPic.get(uuid);
    }
}

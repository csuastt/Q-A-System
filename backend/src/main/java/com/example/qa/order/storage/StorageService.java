package com.example.qa.order.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

public interface StorageService {

    void init();
    void store(MultipartFile file, UUID uuid);
    Stream<Path> loadAll();
    Path load(UUID uuid);
    Resource loadAsResource(UUID uuid);
    void deleteAll();
    void delete(UUID uuid);
    String getNameByUUID(UUID uuid);
}

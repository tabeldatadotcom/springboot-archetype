package com.maryanto.dimas.archetype.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    private final String uploadStorage;
    private final String downloadStorage;

    public StorageService(
            @Value("${storage.files.location.upload}") String uploadStorage,
            @Value("${storage.files.location.download}") String downloadStorage) {
        this.uploadStorage = uploadStorage;
        this.downloadStorage = downloadStorage;
    }

    public File upload(MultipartFile requestFile) throws IOException {
        if (requestFile.isEmpty()) {
            throw new IOFileUploadException("file is can't empty", null);
        }

        String filename = requestFile.getOriginalFilename();

        File newDirectory = new File(this.uploadStorage);
        if (!newDirectory.exists()) {
            newDirectory.mkdirs();
        }

        log.info("path: {}, filename: {}", newDirectory.getPath(), filename);
        File newFile = new File(newDirectory, filename);

        Path path = Paths.get(newDirectory.getPath(), filename);
        requestFile.transferTo(path);

        return newFile;
    }

    public File download(String objectId) {
        StringBuilder fileBuilder = new StringBuilder(File.separator)
                .append(UUID.randomUUID().toString())
                .append(".")
                .append(FilenameUtils.getExtension(objectId));

        File aDirectory = new File(this.downloadStorage);
        if (!aDirectory.exists()) {
            aDirectory.mkdirs();
        }

        File aFile = new File(aDirectory, fileBuilder.toString());
        return aFile;
    }

    public UrlResource download(File aFile) throws MalformedURLException, FileNotFoundException {
        UrlResource resource = new FileUrlResource(aFile.getAbsolutePath());
        if (resource.exists() || resource.isReadable())
            return resource;
        else
            throw new FileNotFoundException("File can't be found in " + aFile.getAbsolutePath());
    }
}

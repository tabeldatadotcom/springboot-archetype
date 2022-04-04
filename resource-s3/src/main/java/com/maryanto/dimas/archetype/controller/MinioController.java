package com.maryanto.dimas.archetype.controller;

import com.maryanto.dimas.archetype.dto.PreviewDTO;
import com.maryanto.dimas.archetype.service.MinioService;
import io.minio.ObjectWriteResponse;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/minio")
@Slf4j
public class MinioController {

    private final MinioService service;
    private final String storageLocation;

    public MinioController(
            MinioService service,
            @Value("${storage.files.location}") String storageLocation) {
        this.service = service;
        this.storageLocation = storageLocation;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @NotNull @NotEmpty @RequestParam("file") MultipartFile file,
            @NotEmpty @RequestParam("folder") String folder) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (file.isEmpty()) {
            throw new IOFileUploadException("file is can't empty", null);
        }

        String filename = file.getOriginalFilename();

        File newDirectory = new File(this.storageLocation);
        if (!newDirectory.exists()) {
            newDirectory.mkdirs();
        }

        log.info("path: {}, filename: {}", newDirectory.getPath(), filename);
        File newFile = new File(newDirectory, filename);

        Path path = Paths.get(newDirectory.getPath(), filename);
        file.transferTo(path);

        ObjectWriteResponse response = this.service.upload(newFile, folder);
        Map<String, Object> body = new HashMap<>();
        body.put("objectId", response.object());
        body.put("versionId", response.versionId());
        body.put("bucket", response.bucket());
        body.put("headers", response.headers().toMultimap());

        if (newFile.exists()) return ResponseEntity.ok().body(body);
        else return ResponseEntity.noContent().build();
    }

    @PostMapping("/preview")
    public ResponseEntity<?> preview(
            @RequestBody @Validated PreviewDTO.PresignedUrlRequest data) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        StatObjectResponse response = this.service.isObjectExists(data.getObjectId());
        if (response.deleteMarker())
            return ResponseEntity.noContent().build();

        String url = this.service.presignedObjectUrl(data);
        PreviewDTO.PresignedUrlResponse body = PreviewDTO.PresignedUrlResponse.builder()
                .url(url)
                .objectId(data.getObjectId())
                .bucketName(response.bucket())
                .build();
        return ResponseEntity.ok(body);
    }
}

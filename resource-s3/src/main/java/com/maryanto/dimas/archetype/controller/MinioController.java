package com.maryanto.dimas.archetype.controller;

import com.maryanto.dimas.archetype.dto.DownloadDTO;
import com.maryanto.dimas.archetype.dto.PreviewDTO;
import com.maryanto.dimas.archetype.service.MinioService;
import io.minio.ObjectWriteResponse;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/minio")
@Slf4j
public class MinioController {

    private final MinioService service;

    public MinioController(
            MinioService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @NotNull @NotEmpty @RequestParam("file") MultipartFile file,
            @NotEmpty @RequestParam("folder") String folder) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ObjectWriteResponse response = this.service.upload(file, folder);
        Map<String, Object> body = new HashMap<>();
        body.put("objectId", response.object());
        body.put("versionId", response.versionId());
        body.put("bucket", response.bucket());
        body.put("headers", response.headers().toMultimap());

        return ResponseEntity.ok().body(body);
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

    @PostMapping("/download")
    public ResponseEntity<?> download(
            @RequestBody @Validated DownloadDTO.DownloadObjectRequest data,
            HttpServletRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        StatObjectResponse response = this.service.isObjectExists(data.getObjectId());
        if (response.deleteMarker())
            return ResponseEntity.noContent().build();

        UrlResource resource = this.service.downloadByObject(data.getObjectId());

        String contentType = request.getServletContext().getMimeType(resource.getFilename());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        StringBuilder contentDisposition = new StringBuilder("attachment; filename=\"").append(resource.getFilename()).append("\"");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }
}

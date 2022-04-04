package com.maryanto.dimas.archetype.service;

import com.maryanto.dimas.archetype.dto.PreviewDTO;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Tags;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class MinioService {

    private final MinioClient minio;
    private final String bucketName;
    private StorageService storageService;

    public MinioService(
            MinioClient minio,
            String bucketName,
            StorageService storageService) {
        this.minio = minio;
        this.bucketName = bucketName;
        this.storageService = storageService;
    }

    public boolean isBucketExists()
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        return this.minio.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
    }

    public void createdBucket() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MakeBucketArgs.Builder builder = MakeBucketArgs
                .builder()
                .bucket(this.bucketName);
        this.minio.makeBucket(builder.build());
    }

    @Deprecated
    public ObjectWriteResponse upload(File file, String folder)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ObjectWriteResponse response = this.minio.uploadObject(UploadObjectArgs.builder()
                .bucket(this.bucketName)
                .filename(file.toPath().toString())
                .tags(Tags.newObjectTags(new HashMap<>()))
                .object(new StringBuilder(folder)
                        .append(File.separator)
                        .append(UUID.randomUUID())
                        .append(".")
                        .append(FilenameUtils.getExtension(file.getName())).toString())
                .build());
        return response;
    }

    public ObjectWriteResponse upload(MultipartFile file, String folder)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        File newFile = this.storageService.upload(file);
        ObjectWriteResponse response = this.minio.uploadObject(UploadObjectArgs.builder()
                .bucket(this.bucketName)
                .filename(newFile.toPath().toString())
                .tags(Tags.newObjectTags(new HashMap<>()))
                .object(new StringBuilder(folder)
                        .append(File.separator)
                        .append(UUID.randomUUID())
                        .append(".")
                        .append(FilenameUtils.getExtension(newFile.getName())).toString())
                .build());
        return response;
    }

    public String presignedObjectUrl(PreviewDTO.PresignedUrlRequest value) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder()
                .bucket(this.bucketName)
                .object(value.getObjectId())
                .expiry(value.getDuration(), value.getUnit())
                .method(Method.GET);
        return this.minio.getPresignedObjectUrl(builder.build());
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public StatObjectResponse isObjectExists(String objectId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        StatObjectArgs.Builder builder = StatObjectArgs.builder()
                .bucket(this.bucketName)
                .object(objectId);
        return this.minio.statObject(builder.build());
    }

    public UrlResource downloadByObject(String objectId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = this.storageService.download(objectId);
        log.info("file will be save on {}", file.getPath());

        DownloadObjectArgs.Builder builder = DownloadObjectArgs.builder()
                .bucket(this.bucketName)
                .object(objectId)
                .filename(file.toPath().toString());
        this.minio.downloadObject(builder.build());

        UrlResource resource = this.storageService.download(file);
        return resource;
    }
}

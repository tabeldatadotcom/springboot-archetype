package com.maryanto.dimas.archetype.service;

import com.maryanto.dimas.archetype.dto.PreviewDTO;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Tags;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

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

    public MinioService(MinioClient minio, String bucketName) {
        this.minio = minio;
        this.bucketName = bucketName;
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

    public String presignedObjectUrl(PreviewDTO.PresignedUrlRequest value) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder()
                .bucket(this.bucketName)
                .object(value.getObjectId())
                .expiry(value.getDuration(), value.getUnit())
                .method(Method.GET);
        return this.minio.getPresignedObjectUrl(builder.build());
    }

    public String getBucketName(){
        return this.bucketName;
    }

    public StatObjectResponse isObjectExists(String objectId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        StatObjectArgs.Builder builder = StatObjectArgs.builder()
                .bucket(this.bucketName)
                .object(objectId);
        return this.minio.statObject(builder.build());
    }
}

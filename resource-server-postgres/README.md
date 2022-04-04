## OAuth 2.0 Resource server for Upload document

Example of Springboot upload/download files using MinIO, you can run locally

```bash
docker-compose -f .docker/docker-compose.yaml --env-file .docker/.env up -d
```

## Support S3 protocol

- [AWS S3](https://aws.amazon.com/s3/)
- [Google Cloud Storage](https://cloud.google.com/storage)
- [MinIO](https://min.io/)

## Workflows

1. Upload document

```mermaid
sequenceDiagram
    autonumber
    participant client AS Client
    participant backend AS Springboot
    participant minio AS MinIO S3 Storage

    activate backend
    activate minio

    client->>backend: Upload image
    Note over client,backend: Sending via Rest API form encripted
    backend->>minio: Sending image
    Note over backend,minio: Sending files via S3 protocol
    minio-->>backend: Response object id
    backend-->>client: Received object id
    deactivate backend
```

2. Presigned image URL string to download

```mermaid
sequenceDiagram
    autonumber
    participant client AS Client
    participant backend AS Springboot
    participant minio AS MinIO S3 Storage

    activate backend
    activate minio

    client->>backend: Get image url by object id
    Note over client,backend: Sending via Rest API POST request
    backend->>minio: PresignedUrl(objectId)
    Note over backend,minio: Get image url using presignedUrl
    minio-->>backend: Response URL
    
    backend-->>client: Received image Presigned URL with timeout
    deactivate backend
    client->>client: Render image from Response URL
```


## Maintainers

- Dimas Maryanto <software.dimas_m@icloud.com>

if you want to contribute please email me.

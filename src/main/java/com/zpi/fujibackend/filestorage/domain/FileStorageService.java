package com.zpi.fujibackend.filestorage.domain;

import com.zpi.fujibackend.common.exception.FileStorageException;
import com.zpi.fujibackend.common.exception.NotFoundException;
import com.zpi.fujibackend.filestorage.FileStorageFacade;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
class FileStorageService implements FileStorageFacade {

    private final MinioClient minioClient;
    private final String bucketName;

    public FileStorageService(MinioClient minioClient, @Value("${MINIO_BUCKET_NAME}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    public InputStream downloadFile(String objectKey) {
        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build();

            return minioClient.getObject(args);

        } catch (ErrorResponseException e) {
            throw new NotFoundException("File not found on Minio: " + objectKey, e);
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new FileStorageException("Error while downloading file from Minio", e);

        }
    }


}

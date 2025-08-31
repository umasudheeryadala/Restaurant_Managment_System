package com.tastes_of_india.restaurantManagement.service.util.storage;

import io.minio.*;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service("minioStorage")
public class MinioStorage implements StorageStrategy{

    private final MinioClient minioClient;

    private final Logger LOG= LoggerFactory.getLogger(MinioStorage.class);

    @Value("${minio.bucket}") private String bucket;

    public MinioStorage(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public List<String> saveFiles(List<MultipartFile> files, Long id) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        List<String> fileNames=new ArrayList<>();
        for(MultipartFile file:files) {
            String fileName=id + "_" + file.getOriginalFilename();
            // Upload file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            fileNames.add(fileName);
        }
        return fileNames;

    }

    @Override
    public void deleteFiles(List<String> files) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (String file:files){
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .object(file)
                            .bucket(bucket)
                            .build()
            );
        }
    }

    @Override
    public String saveFile(byte[] data, String filename) {
        // Upload file
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename) // 👈 custom file name
                            .stream(bais, data.length, -1)
                            .contentType("application/pdf")
                            .build()
            );
        } catch (Exception e) {
            LOG.debug("Exception while saving file {}",e.getMessage());
        }
        return filename;
    }

    @Override
    public byte[] getFile(String filename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .build()
        ).readAllBytes();
    }
}

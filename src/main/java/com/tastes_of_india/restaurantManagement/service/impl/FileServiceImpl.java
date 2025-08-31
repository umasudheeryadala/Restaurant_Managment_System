package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.service.FileService;
import com.tastes_of_india.restaurantManagement.service.util.storage.StorageStrategy;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private StorageStrategy storageStrategy;

    public FileServiceImpl(ApplicationContext applicationContext, @Value("${app.storage}") String storageType){
        this.storageStrategy= (StorageStrategy) applicationContext.getBean(storageType+"Storage");
    }
    @Override
    public List<String> saveFiles(List<MultipartFile> files, Long id) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return storageStrategy.saveFiles(files,id);
    }

    @Override
    public void deleteFiles(List<String> files) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        storageStrategy.deleteFiles(files);
    }


    @Override
    public String saveFile(byte[] data, String filename) throws IOException {
        return storageStrategy.saveFile(data,filename);
    }

    @Override
    public byte[] getFile(String filename) throws BadRequestAlertException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return storageStrategy.getFile(filename);
    }
}

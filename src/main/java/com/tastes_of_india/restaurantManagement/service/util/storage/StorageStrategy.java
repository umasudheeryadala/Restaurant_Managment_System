package com.tastes_of_india.restaurantManagement.service.util.storage;

import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface StorageStrategy {

    List<String> saveFiles(List<MultipartFile> files, Long id) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void deleteFiles(List<String> files) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    String saveFile(byte[] data,String filename) throws IOException;

    byte[] getFile(String filename) throws BadRequestAlertException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}

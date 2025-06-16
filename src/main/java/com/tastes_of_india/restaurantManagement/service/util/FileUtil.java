package com.tastes_of_india.restaurantManagement.service.util;

import com.tastes_of_india.restaurantManagement.domain.Image;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class FileUtil {

    private static final String uploadDir = "uploads/";

    public static List<String> saveFiles(List<MultipartFile> files,Long id) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = id + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            imageUrls.add(fileName);
        }
        return imageUrls;
    }

    public static String saveFile(MultipartFile file,Long id) throws IOException {
        String fileName = id + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public static void deleteFiles(List<String> files) throws IOException {
        Set<Image> images = new HashSet<>();
        for (String file : files) {
            if(file==null) continue;
            Path filePath = Paths.get(uploadDir + file);
            Files.createDirectories(filePath.getParent());
            Files.delete(filePath);
        }
    }

    public static void deleteFile(String file) throws IOException {
        if(file==null) return;;
        Path filePath = Paths.get(uploadDir + file);
        Files.createDirectories(filePath.getParent());
        Files.delete(filePath);

    }

    public static String saveFile(byte[] data,String filename) throws IOException {
        Path filPath = Paths.get(uploadDir+ filename);

        Files.createDirectories(filPath.getParent());

        Files.write(filPath,data);

        return filename;
    }

    public static byte[] getFile(String filename) throws IOException, BadRequestAlertException {
        Path filePath=Paths.get(uploadDir+filename);
        try {
            byte[] imageBytes = Files.readAllBytes(filePath);
            return imageBytes;
        }catch (Exception e){
            throw new BadRequestAlertException("Unable to Read File","FileReader","unableToRead");
        }
    }


}

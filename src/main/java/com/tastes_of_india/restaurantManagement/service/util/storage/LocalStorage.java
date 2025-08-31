package com.tastes_of_india.restaurantManagement.service.util.storage;

import com.tastes_of_india.restaurantManagement.domain.Image;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service("localStorage")
public class LocalStorage implements StorageStrategy {

    private static final String uploadDir = "uploads/";

    public List<String> saveFiles(List<MultipartFile> files,Long id) throws IOException {
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

    public void deleteFiles(List<String> files) throws IOException {
        Set<Image> images = new HashSet<>();
        for (String file : files) {
            if(file==null) continue;
            Path filePath = Paths.get(uploadDir + file);
            Files.createDirectories(filePath.getParent());
            Files.delete(filePath);
        }
    }

    public String saveFile(byte[] data,String filename) throws IOException {
        Path filPath = Paths.get(uploadDir+ filename);

        Files.createDirectories(filPath.getParent());

        Files.write(filPath,data);

        return filename;
    }

    public byte[] getFile(String filename) throws BadRequestAlertException {
        Path filePath=Paths.get(uploadDir+filename);
        try {
            byte[] imageBytes = Files.readAllBytes(filePath);
            return imageBytes;
        }catch (Exception e){
            throw new BadRequestAlertException("Unable to Read File","FileReader","unableToRead");
        }
    }


}

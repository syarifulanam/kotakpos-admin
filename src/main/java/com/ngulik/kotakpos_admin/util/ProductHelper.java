package com.ngulik.kotakpos_admin.util;

import com.ngulik.kotakpos_admin.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class ProductHelper {

    public static String generateBarcode(Product product) {
        String storeCode = "000";
        String categoryCode = String.format("%03d", product.getCategory().getId());
        String productIncrement = String.format("%09d", product.getId());
        return storeCode + categoryCode + productIncrement;
    }

    public static String saveImage(MultipartFile image, Product product,
                                   String uploadDir,
                                   List<String> allowedExtensions,
                                   long maxFileSize) throws IOException {

        if (image == null || image.isEmpty()) {
            return null;
        }

        validateImage(image, maxFileSize, allowedExtensions);

        String originalFilename = image.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String slugName = toSlug(product.getName());
        String newFileName = "prd-" + System.currentTimeMillis() + "-" + slugName + fileExtension;

        Path uploadPath = Paths.get(uploadDir, "products");
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path filePath = uploadPath.resolve(newFileName);
        try {
            Files.copy(image.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "/uploads/products/" + newFileName;
    }

    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "_");
    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private static void validateImage(MultipartFile image,
                                      long maxFileSize,
                                      List<String> allowedExtensions) {

        if (image.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds the limit");
        }

        String fileExtension = getFileExtension(image.getOriginalFilename());

        boolean isValid = false;
        for (String ext : allowedExtensions) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new RuntimeException("Invalid file type. Only " + allowedExtensions + " are allowed.");
        }
    }
}
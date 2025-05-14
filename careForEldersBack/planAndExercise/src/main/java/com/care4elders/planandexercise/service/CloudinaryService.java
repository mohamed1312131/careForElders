package com.care4elders.planandexercise.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file, String folderName, String resourceType) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File to upload cannot be null or empty");
        }

        // Basic validation for resourceType (can be expanded)
        if (!"auto".equals(resourceType) && !"image".equals(resourceType) && !"video".equals(resourceType)) {
            throw new IllegalArgumentException("Invalid resource_type specified. Must be 'auto', 'image', or 'video'.");
        }

        // Validate file type based on resourceType more explicitly if needed
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("File content type cannot be determined.");
        }

        if ("image".equals(resourceType) && !contentType.startsWith("image/")) {
            logger.warn("Attempting to upload non-image file ({}) as image.", contentType);
            throw new IllegalArgumentException("Only image files are allowed for resource_type 'image'. Detected: " + contentType);
        }
        if ("video".equals(resourceType) && !contentType.startsWith("video/")) {
            logger.warn("Attempting to upload non-video file ({}) as video.", contentType);
            throw new IllegalArgumentException("Only video files are allowed for resource_type 'video'. Detected: " + contentType);
        }


        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType, // "image", "video", or "auto"
                            "folder", "planandexercise/" + folderName, // e.g., "planandexercise/exercise_images"
                            "public_id", generatePublicId(file.getOriginalFilename())
                            // You can add more transformations or settings here
                    )
            );
            String secureUrl = (String) uploadResult.get("secure_url");
            logger.info("File uploaded successfully to Cloudinary. URL: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            logger.error("Failed to upload file {} to Cloudinary folder {}", file.getOriginalFilename(), folderName, e);
            throw new IOException("Failed to upload file to Cloudinary: " + file.getOriginalFilename(), e);
        }  catch (Exception e) {
            logger.error("An unexpected error occurred during Cloudinary upload for file {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Cloudinary upload failed for file: " + file.getOriginalFilename(), e);
        }
    }

    private String generatePublicId(String originalFilename) {
        String nameWithoutExtension = originalFilename;
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex != -1) {
            nameWithoutExtension = originalFilename.substring(0, lastDotIndex);
        }
        // Replace non-alphanumeric characters (except underscore) from filename to make it URL-friendly
        String sanitizedFilename = nameWithoutExtension.replaceAll("[^a-zA-Z0-9_]", "_");
        return sanitizedFilename + "_" + UUID.randomUUID().toString();
    }

    // Convenience methods for specific types
    public String uploadImage(MultipartFile imageFile) throws IOException {
        return uploadFile(imageFile, "exercise_images", "image");
    }

    public String uploadVideo(MultipartFile videoFile) throws IOException {
        return uploadFile(videoFile, "exercise_videos", "video");
    }
}
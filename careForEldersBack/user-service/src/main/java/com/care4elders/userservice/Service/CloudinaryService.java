package com.care4elders.userservice.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            // Upload to Cloudinary with some default settings
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", "user_uploads",
                            "public_id", generatePublicId(file.getOriginalFilename())
                    )
            );

            // Return secure URL
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new IOException("Failed to upload file to Cloudinary", e);
        }
    }

    private String generatePublicId(String originalFilename) {
        // Generate a unique public ID based on timestamp and original filename
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        return "user_upload_" + timestamp + "_" + nameWithoutExtension;
    }
}

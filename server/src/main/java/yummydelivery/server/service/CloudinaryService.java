package yummydelivery.server.service;

import com.cloudinary.Cloudinary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yummydelivery.server.exceptions.ApiException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public String uploadImage(MultipartFile image, String beverageName) {
        Map<String, String> options = new HashMap<>();
        options.put("folder", "YummyDeliveryImages");
        options.put("public_id", beverageName);
        String transformation = "w_300,h_300,c_scale";
        options.put("transformation", transformation);
        try {
            File file = convertToFile(image);
            @SuppressWarnings("unchecked")
            Map<String, String> uploadResult = cloudinary.uploader().upload(file, options);
            return uploadResult.get("url");
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload the image: " + e.getMessage());
        }
    }

    private File convertToFile(MultipartFile image) {
        File file;
        try {
            file = File.createTempFile("temp-file", image.getOriginalFilename());
            image.transferTo(file);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "File converting failed: " + e.getMessage());
        }
        return file;
    }

    public void validateImageFile(MultipartFile productImage) {
        boolean isValid = productImage != null &&
                productImage.getContentType() != null &&
                (productImage.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) ||
                        productImage.getContentType().equals(MediaType.IMAGE_PNG_VALUE));
        if (!isValid) {
            throw new IllegalArgumentException("Product image media type must be Jpeg or Png");
        }
        if (productImage.getSize() > 0) {
            throw new IllegalArgumentException("Multiple files selected.");
        }
    }
}

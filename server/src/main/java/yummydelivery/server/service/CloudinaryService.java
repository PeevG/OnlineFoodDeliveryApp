package yummydelivery.server.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yummydelivery.server.exceptions.ApiException;
import yummydelivery.server.exceptions.CloudinaryException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public String uploadImage(MultipartFile image, String imageName) {
        Map<String, String> options = new HashMap<>();
        options.put("folder", "YummyDeliveryImages");
        options.put("public_id", imageName.trim().replaceAll(" ", ""));
        try {
            File file = resizeAndConvertToFile(image);
            @SuppressWarnings("unchecked")
            Map<String, String> uploadResult = cloudinary.uploader().upload(file, options);
            return uploadResult.get("url");
        } catch (IOException e) {
            throw new CloudinaryException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload the image: " + e.getMessage());
        }
    }

    public void validateImageFile(MultipartFile productImage) {
        boolean isValid = productImage != null &&
                !productImage.isEmpty() &&
                productImage.getContentType() != null &&
                (productImage.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) ||
                        productImage.getContentType().equals(MediaType.IMAGE_PNG_VALUE));
        log.debug("Content type should be either: " + MediaType.IMAGE_JPEG_VALUE);
        log.debug("or: " + MediaType.IMAGE_PNG_VALUE);
        log.debug("Product image is null:" + (productImage == null));
        log.debug("Product content type is null:" + (productImage.getContentType() == null));
        log.debug("Current content type is: " + productImage.getContentType());
        log.debug("Product image is empty: " + productImage.isEmpty());
        if (!isValid) {
            throw new IllegalArgumentException("Product image media type must be Jpeg or Png");
        }
    }

    public void deleteProductImageFromCloudinary(String productImageURL) {
        String urlPublicKey = getCloudinaryPublicId(productImageURL);
        try {
            cloudinary.uploader().destroy(urlPublicKey, ObjectUtils.emptyMap());
            log.info("Image successfully deleted from Cloudinary");
        } catch (IOException e) {
            log.info("Failed to delete product image from Cloudinary");
            throw new CloudinaryException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete product image from Cloudinary");
        }
    }

    private String getCloudinaryPublicId(String productImageURL) {
        String substring = productImageURL.substring(productImageURL.lastIndexOf("/") + 1, productImageURL.length() - 4);
        return "YummyDeliveryImages/" + substring;
    }

    protected File resizeAndConvertToFile(MultipartFile productImage) {
        BufferedImage bufferedImage;
        File file;
        try {
            bufferedImage = ImageIO.read(productImage.getInputStream());
            BufferedImage resized = Scalr.resize(bufferedImage, Scalr.Method.BALANCED, Scalr.Mode.AUTOMATIC, 350, 350);
            file = File.createTempFile("temp-file", productImage.getOriginalFilename());

            String format = FilenameUtils.getExtension(productImage.getOriginalFilename());
            if (format == null) format = "jpg";
            ImageIO.write(resized, format, file);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "File converting failed: " + e.getMessage());
        }
        return file;
    }
}

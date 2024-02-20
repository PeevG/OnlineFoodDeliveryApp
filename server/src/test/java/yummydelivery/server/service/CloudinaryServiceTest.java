package yummydelivery.server.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {
    @Mock
    private Cloudinary cloudinary;
    @InjectMocks
    public CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void validateImageFile_InvalidImageType_IllegalArgumentExceptionThrown() {
        MockMultipartFile file = new MockMultipartFile(
                "image", "test-image.jpg", "text/plain", new byte[]{});

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cloudinaryService.validateImageFile(file));
        assertEquals("Product image media type must be Jpeg or Png", exception.getMessage());
    }

    @Test
    public void validateImageFile_ImageIsEmpty_IllegalArgumentExceptionThrown() {
        MockMultipartFile image = new MockMultipartFile(
                "image", "test-image.jpg", MediaType.IMAGE_PNG_VALUE, new byte[]{});

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cloudinaryService.validateImageFile(image));
        assertEquals("Product image media type must be Jpeg or Png", exception.getMessage());
    }

    @Test
    public void validateImageFile_Success() {
        MockMultipartFile image = new MockMultipartFile(
                "image", "test-image.jpg", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});

        assertDoesNotThrow(() -> cloudinaryService.validateImageFile(image));
    }

    @Test
    void uploadImage_Success() throws IOException {
        File file = ResourceUtils.getFile("classpath:testImage.jpg");
        byte[] fileContent = StreamUtils.copyToByteArray(new FileInputStream(file));
        MultipartFile image = new MockMultipartFile(
                "burger",
                "testfile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                fileContent
        );

        Map<String, String> uploadResult = new HashMap<>();
        uploadResult.put("url", "cloudinary/blabla/bla/burger.jpg");

        Uploader uploaderMock = mock();
        when(cloudinary.uploader()).thenReturn(uploaderMock);
        when(uploaderMock.upload(any(File.class), anyMap())).thenReturn(uploadResult);

        String imageURL = cloudinaryService.uploadImage(image, image.getName());

        assertNotNull(imageURL);
        assertTrue(imageURL.endsWith("burger.jpg"));
        verify(uploaderMock, times(1)).upload(any(File.class), anyMap());
    }

    @Test
    void deleteProductImageFromCloudinary_Success() throws IOException {
        String productImageURL = "cloudinary/qwelkqew/YummyDeliveryImages/pizza.jpg";
        String urlPublicKey = "YummyDeliveryImages/pizza";

        Uploader uploaderMock = mock();
        when(cloudinary.uploader()).thenReturn(uploaderMock);
        cloudinaryService.deleteProductImageFromCloudinary(productImageURL);

        verify(cloudinary.uploader(), times(1)).destroy(urlPublicKey, ObjectUtils.emptyMap());
    }
}
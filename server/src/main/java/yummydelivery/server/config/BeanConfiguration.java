package yummydelivery.server.config;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class BeanConfiguration {
    @Value("${CLOUDINARY_CLOUD_NAME}")
    private String CLOUDINARY_CLOUD_NAME;
    @Value("${CLOUDINARY_API_KEY}")
    private String CLOUDINARY_API_KEY;
    @Value("${CLOUDINARY_API_SECRET}")
    private String CLOUDINARY_API_SECRET;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Cloudinary cloudinary() {
        String cloudinaryURL = "cloudinary://" + CLOUDINARY_API_KEY + ":" + CLOUDINARY_API_SECRET + "@" + CLOUDINARY_CLOUD_NAME;
        return new Cloudinary(cloudinaryURL);
    }
}

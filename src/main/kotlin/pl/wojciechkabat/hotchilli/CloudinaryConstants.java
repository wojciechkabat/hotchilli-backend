package pl.wojciechkabat.hotchilli;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:cloudinary-config.properties")
public class CloudinaryConstants {
    public static String CLOUDINARY_CLOUD_NAME;
    public static String CLOUDINARY_API_KEY;
    public static String CLOUDINARY_API_SECRET;

    @Value("${hotchilli.cloudinary.cloudname}")
    public void setCloudinaryCloudName(String cloudinaryCloudName) {
        CLOUDINARY_CLOUD_NAME = cloudinaryCloudName;
    }

    @Value("${hotchilli.cloudinary.apiKey}")
    public void setCloudinaryApiKey(String cloudinaryApiKey) {
        CLOUDINARY_API_KEY = cloudinaryApiKey;
    }

    @Value("${hotchilli.cloudinary.apiSecret}")
    public void setCloudinaryApiSecret(String cloudinaryApiSecret) {
        CLOUDINARY_API_SECRET = cloudinaryApiSecret;
    }
}

package pl.wojciechkabat.hotchilli;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:facebook-config.properties")
public class FacebookConstants {
    public static Long FACEBOOK_APP_ID;
    public static String FACEBOOK_ACCESS_TOKEN;
    public static final String FACEBOOK_ME_BASE_URL = "https://graph.facebook.com/v3.2/me";
    public static final String FACEBOOK_PICTURE_LINK = "https://graph.facebook.com/v3.2/";
    public static final String FACEBOOK_PICTURE_LINK_ENDING = "/picture?type=large";

    @Value("${hotchilli.facebook.appId}")
    public void setFacebookAppId(Long facebookAppId) {
        FACEBOOK_APP_ID = facebookAppId;
    }

    @Value("${hotchilli.facebook.accessToken}")
    public void setFacebookAccessToken(String facebookAccessToken) {
        FACEBOOK_ACCESS_TOKEN = facebookAccessToken;
    }
}

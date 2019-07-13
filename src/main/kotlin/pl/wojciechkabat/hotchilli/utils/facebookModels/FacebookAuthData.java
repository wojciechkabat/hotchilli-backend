package pl.wojciechkabat.hotchilli.utils.facebookModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class FacebookAuthData {
    @JsonProperty("app_id")
    private Long appId;
    @JsonProperty("applicationName")
    private String applicationName;
    @JsonProperty("is_valid")
    private boolean isValid;
    @JsonProperty("user_id")
    private String userId;
}

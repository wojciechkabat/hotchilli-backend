package pl.wojciechkabat.hotchilli.utils.facebookModels.responseModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookAuthData;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class FacebookAuthResponse {
    @JsonProperty("data")
    private FacebookAuthData facebookAuthData;
}

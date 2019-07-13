package pl.wojciechkabat.hotchilli.utils.facebookModels.responseModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookPicture;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class FacebookPictureResponse {
    @JsonProperty("data")
    private FacebookPicture facebookPicture;
}

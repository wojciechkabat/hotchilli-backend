package pl.wojciechkabat.hotchilli.utils.facebookModels.responseModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookPhoto;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookPhotosResponse {
    @JsonProperty("data")
    private List<FacebookPhoto> facebookPhotos;

    public List<FacebookPhoto> getFacebookPhotos() {
        return facebookPhotos;
    }
}

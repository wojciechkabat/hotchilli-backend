package pl.wojciechkabat.hotchilli.utils.facebookModels.responseModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.wojciechkabat.hotchilli.utils.facebookModels.FacebookAlbum;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookAlbumsResponse {
    @JsonProperty("data")
    private List<FacebookAlbum> facebookAlbums;

    public List<FacebookAlbum> getFacebookAlbums() {
        return facebookAlbums;
    }
}

package pl.wojciechkabat.hotchilli.utils.facebookModels;

import lombok.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "aFacebookAlbum")
public class FacebookAlbum {
    private Long id;
    private String name;
    private String type;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
